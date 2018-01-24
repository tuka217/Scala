package forms

import scala.concurrent.{Await, Future}
import play.api.data.Forms._
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.text
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.email
import play.api.data.validation._
import models.User

object SignUpFormData {

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

  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText(minLength = 5).verifying(passwordCheckConstraint),
    )(Data.apply)(Data.unapply)
  )

  case class Data(firstName: String,
                  lastName: String,
                  email: String,
                  password: String)

}
