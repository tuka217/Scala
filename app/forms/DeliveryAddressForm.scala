package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.nonEmptyText

object DeliveryAddressForm {

  val form = Form(
      mapping(
        "street" -> nonEmptyText,
        "city" -> nonEmptyText,
        "postalcode" -> number,
        "country" -> nonEmptyText,
      )(Data.apply)(Data.unapply)
  )

  case class Data(street: String, city: String, postalcode: Int, country: String)
}
