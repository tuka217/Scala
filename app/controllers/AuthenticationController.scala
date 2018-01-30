package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.services.{AuthenticatorResult, AuthenticatorService}
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.{SignInForm,SignUpFormData}
import dao.UserService
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import utils.DefaultEnv
import com.mohiva.play.silhouette.api.{EventBus, LoginEvent}
import scala.concurrent.{ExecutionContext, Future}
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import scala.util.{Success, Failure}
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.email
import play.api.data.validation._
import models.User

class AuthenticationController @Inject()(cc: ControllerComponents,
                                         userService: UserService,
                                         silhouette: Silhouette[DefaultEnv],
                                         credentialsProvider: CredentialsProvider)
                                        (implicit ec: ExecutionContext)
  extends AbstractController(cc)
    with I18nSupport {

  def env: Environment[DefaultEnv] = silhouette.env

  def signUpForm = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.signup(SignUpFormData.form)))
  }

  def signUpSubmit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>

    SignUpFormData.form.bindFromRequest.fold(
      (hasErrors: Form[SignUpFormData.Data]) => Future.successful(BadRequest(views.html.signup(hasErrors))),

      (success: SignUpFormData.Data) => {

        userService.retrieve(LoginInfo(CredentialsProvider.ID, success.email))
          .map {
            case Some(u:User) => {
              BadRequest(views.html.signup(SignUpFormData.form.fill(success).withError("email", "account with this email already exists")))
            }
            case None => {
              userService.create(success).flatMap(env.authenticatorService.create(_))
                .flatMap(env.authenticatorService.init(_))
                .flatMap(env.authenticatorService.embed(_, Redirect(routes.Application.index())))
              AuthenticatorResult(Redirect(routes.AuthenticationController.signInForm))
            }
          }
      })
  }

  def signInForm = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(views.html.signin(SignInForm.form)))
  }

  def signInSubmit = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>

    SignInForm.form.bindFromRequest.fold(

      hasErrors => {
        Future.successful(BadRequest(views.html.signin(hasErrors)))
      },

      success => {
        credentialsProvider.authenticate(credentials = Credentials(success.email, success.password))
          .flatMap { loginInfo =>
            userService.retrieve(loginInfo).flatMap {
              case Some(user) => for {
                authenticator <- env.authenticatorService.create(loginInfo).map{case authenticator => authenticator}
                cookie <- env.authenticatorService.init(authenticator)
                result <- env.authenticatorService.embed(cookie, Redirect(routes.Application.menu()))
              } yield {
                env.eventBus.publish(LoginEvent(user, request))
                result
              }
              case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
            }
          }.recover {
          case e: Exception =>
            Redirect(routes.AuthenticationController.signInForm()).flashing("login-error" -> "Wrong credentials!")
        }
      }
    )
  }

  def signOut = silhouette.SecuredAction.async { implicit request =>
    env.authenticatorService.discard(request.authenticator, Redirect(routes.Application.hello))
  }
}
