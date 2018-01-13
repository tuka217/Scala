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
import play.api.mvc.Flash
import scala.util.{Failure, Success}

class DeliveryAddressController @Inject() (
                              deliveryAddressDAO: DeliveryAddressDAO,
                              messagesAction: MessagesActionBuilder,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext2: ExecutionContext) extends AbstractController(controllerComponents) with controllers.BaseController {

  val executionContext = executionContext2

  val deliveryAddressForm = Form(
    mapping(
      "id" -> ignored(23L),
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "street" -> nonEmptyText,
      "city" -> nonEmptyText,
      "postalcode" -> number,
      "country" -> nonEmptyText,
      "userId" -> ignored(23L)
    )(DeliveryAddress.apply)(DeliveryAddress.unapply)
  )

  val UsersList = Redirect(routes.UserController.list())

  def deliveryAddressPost(userId: Long) = authenticated { implicit request =>
    deliveryAddressForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.createDeliveryAddress(formWithErrors, userId))
      },
      deliveryAddressData => {
        val deliveryAddress: DeliveryAddress = DeliveryAddress(
          0,
          deliveryAddressData.firstName,
          deliveryAddressData.lastName,
          deliveryAddressData.street,
          deliveryAddressData.city,
          deliveryAddressData.postalcode,
          deliveryAddressData.country,
          userId
        )

        //        val future = deliveryAddressDAO.insert(deliveryAddress)
        //
        //          .onComplete {
        //          case Success(value) => value
        //          case Failure(e) => null
        //        }
        //        val result = Await.result(future, 500000 millis)
        //
        //        if (result != null) {
        //          Redirect(routes.DeliveryAddressController.list(userId))
        //        } else {
        //
        //          BadRequest("Opps! Something went wrong during saving delivery address")
        //        }
        //      }
        val future = deliveryAddressDAO.insert(deliveryAddress).map { case (deliveryAddresses) => Redirect(routes.DeliveryAddressController.list(userId)) }
        Await.result(future, 500000 millis)
      }
    )
  }

  def deliveryAddressGet(userId: Long) = authenticated {implicit request =>
    Ok(views.html.createDeliveryAddress(deliveryAddressForm, userId))
  }

  def list(userId: Long) = authenticated { implicit request =>
    val future =  deliveryAddressDAO.findByUserId(userId).map { case (deliveryAddresses) => Ok(views.html.listOfDeliveryAddresses(deliveryAddresses, userId)) }
    Await.result(future, 500000 millis)
  }

  def deliveryAddressPostUpdate(id: Long) = authenticated { implicit request =>
    deliveryAddressForm.bindFromRequest.fold(
      formWithErrors => {
          BadRequest(views.html.editDeliveryAddress(formWithErrors, id))
      },
    deliveryAddressData => {

      val future = deliveryAddressDAO.update(id, deliveryAddressData).map { case (deliveryAddress) => Redirect(routes.DeliveryAddressController.list(deliveryAddressData.userId)) }
      Await.result(future, 500000 millis)

//      Redirect(routes.UserController.list())
    })
  }

  def deliveryAddressGetUpdate(id: Long) = authenticated { implicit request =>

    val future =  deliveryAddressDAO.findById(id).map { case (deliveryAddresses) =>
      deliveryAddresses match {
        case Some(value) => Ok(views.html.editDeliveryAddress(deliveryAddressForm.fill(value), id))
        case None => NotFound
      }
    }

    Await.result(future, 500000 millis)
  }
}
