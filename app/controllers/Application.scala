package controllers

import dao.UserService
import javax.inject.Inject

import models.User
import play.api.data.validation._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.email
import play.api.mvc.{ AbstractController, ControllerComponents }
import play.api.data._
import scala.concurrent.ExecutionContext
import scala.concurrent._
import scala.concurrent.duration._
import play.api.mvc._
import play.api.data.Forms._
import slick._
import com.mohiva.play.silhouette.api.services.{AuthenticatorResult, AuthenticatorService}
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import utils.DefaultEnv

class Application @Inject() (
                              userService: UserService,
                              silhouette: Silhouette[DefaultEnv],
                              messagesAction: MessagesActionBuilder,
                              credentialsProvider: CredentialsProvider,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext: ExecutionContext)
  extends AbstractController(controllerComponents) with play.api.i18n.I18nSupport {

  val authService: AuthenticatorService[CookieAuthenticator] = silhouette.env.authenticatorService

  def index =  silhouette.SecuredAction.async { implicit request =>
    userService.all().map { case (users) => Ok(views.html.index(users)) }
  }
}
