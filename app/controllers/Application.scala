package controllers

import dao.UsersDaoImpl

import javax.inject.Inject
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
import models._

class Application @Inject() (
                              userDao: UsersDaoImpl,
                              messagesAction: MessagesActionBuilder,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext: ExecutionContext) extends AbstractController(controllerComponents) with play.api.i18n.I18nSupport {

  val userForm = Form(
    mapping(
      "username" -> ignored(""),
      "email" -> email,
      "password" -> nonEmptyText,
      "id" ->  ignored(23L)
    )(User.apply)(User.unapply)
  )

  def login = Action.async{implicit request =>
    Future {
      Ok(views.html.login(userForm))
    } (executionContext)
  }

  def loginPost = Action.async{implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.create(formWithErrors))
        }(executionContext)

      },
      userData => {

        val future: Future[Option[User]] = userDao.findByEmailAndPassword(userData.email, userData.password)

        future.map(user => {
          user match {
            case None => Redirect (routes.Application.login () )
            case Some(s: User) => Redirect (routes.UserController.list () ).withSession (request.session + ("user" -> UserObject.serializeUser (userData) ) )
          }


        })
      }
    )
  }
}
