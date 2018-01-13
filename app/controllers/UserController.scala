package controllers

import dao.UsersDaoImpl
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
import scala.concurrent.Future
import scala.util.{Success,Failure}

class UserController @Inject() (
                              userDao: UsersDaoImpl,
                              messagesAction: MessagesActionBuilder,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext2: ExecutionContext) extends AbstractController(controllerComponents) with controllers.BaseController {

  val executionContext = executionContext2
  val hasSpecialCharacter = """[A-Za-z0-9]*""".r
  val noBigLetter = """[a-z0-9]*$""".r

  val passwordCheckConstraint: Constraint[String] = Constraint("")({
    plainText =>
      val errors = plainText match {
        case noBigLetter() => Seq(ValidationError("Password must have at least one capital letter"))
        case hasSpecialCharacter() => Seq(ValidationError("Password must have at least one special character"))
        case _ => Nil
      }

      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })

  val usernameCheckConstraint: Constraint[String] = Constraint("")({ plainText =>
    val future: Future[Option[User]] = userDao.findByUsername(plainText)
    val response =  Await.result(future, 500000 millis);
    val errors = response match {
      case None => Nil
      case Some(s: User) => Seq(ValidationError("Given username already taken"))
      case _ => Nil
    }

    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })

  val emailCheckConstraint: Constraint[String] = Constraint("")({ plainText =>
    val future: Future[Option[User]] = userDao.findByEmail(plainText)
    val response =  Await.result(future, 500000 millis);
    val errors = response match {
      case None => Nil
      case Some(s: User) => Seq(ValidationError("Given email already registered"))
      case _ => Nil
    }

    if (errors.isEmpty) {
      Valid
    } else {
      Invalid(errors)
    }
  })

  val userForm = Form(
    mapping(
      "username" -> nonEmptyText.verifying(usernameCheckConstraint),
      "email" -> email.verifying(emailCheckConstraint),
      "password" -> nonEmptyText(minLength = 5).verifying(passwordCheckConstraint),
      "id" ->  ignored(23L)
    )(User.apply)(User.unapply)
  )

  def list = authenticated { implicit request =>
    var users: Seq[User] = Nil
    userDao.all().map{case (result) => users = result}
    Ok(views.html.index(users))
  }

  def userPost = Action.async {implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {

        Future {
          BadRequest(views.html.create(formWithErrors))
        }(executionContext)

      },
      userData => {
        val user: User = models.User(userData.username, userData.email, userData.password, 0)
        userDao.insert(user).map(_ => Redirect(routes.UserController.list))
      }
    )
  }

  def userGet = Action {implicit request =>
    Ok(views.html.create(userForm))
  }

}