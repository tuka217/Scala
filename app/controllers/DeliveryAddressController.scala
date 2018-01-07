package controllers

import dao.DeliveryAddressDAO
import javax.inject.Inject

import models.DeliveryAddress
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

class DeliveryAddressController @Inject() (
                              deliveryAddressDAO: DeliveryAddressDAO,
                              messagesAction: MessagesActionBuilder,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext: ExecutionContext) extends AbstractController(controllerComponents) with play.api.i18n.I18nSupport {

  val deliveryAddressForm = Form(
    mapping(
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "postalcode" -> number,
      "country" -> nonEmptyText,
      "userId" -> ignored(23L)
    )(DeliveryAddress.apply)(DeliveryAddress.unapply)
  )

  def deliveryAddressPost(userId: Long) = Action.async {implicit request =>
    deliveryAddressForm.bindFromRequest.fold(
      formWithErrors => {

        println("potato")
        Future {
          BadRequest(views.html.createDeliveryAddress(formWithErrors, userId))
        }(executionContext)

      },
      deliveryAddressData => {
        val deliveryAddress: DeliveryAddress = DeliveryAddress(
          deliveryAddressData.firstName,
          deliveryAddressData.lastName,
          deliveryAddressData.street,
          deliveryAddressData.city,
          deliveryAddressData.postalcode,
          deliveryAddressData.country,
          userId
        )
        deliveryAddressDAO.insert(deliveryAddress).map(_ => Redirect(routes.DeliveryAddressController.list(userId)))
      }
    )
  }

  def deliveryAddressGet(userId: Long) = Action {implicit request =>
    Ok(views.html.createDeliveryAddress(deliveryAddressForm, userId))
  }

  def list(userId: Long) = Action.async { implicit request =>
    deliveryAddressDAO.findByUserId(userId).map { case (deliveryAddresses) => Ok(views.html.listOfDeliveryAddresses(deliveryAddresses, userId)) }
  }
}