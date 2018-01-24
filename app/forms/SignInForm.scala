package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.email

object SignInForm {

  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(email: String,
                  password: String)

}

