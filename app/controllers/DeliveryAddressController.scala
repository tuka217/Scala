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
import com.mohiva.play.silhouette.api.services.{AuthenticatorResult, AuthenticatorService}
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import utils.DefaultEnv
import forms.DeliveryAddressForm
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import scala.concurrent.Future

class DeliveryAddressController @Inject() (
        deliveryAddressDao: DeliveryAddressDAO,
        silhouette: Silhouette[DefaultEnv],
        messagesAction: MessagesActionBuilder,
        credentialsProvider: CredentialsProvider,
        controllerComponents: ControllerComponents
      )(implicit executionContext: ExecutionContext) extends AbstractController(controllerComponents) with play.api.i18n.I18nSupport {


  def deliveryAddressCreatePost(userId: Int) = silhouette.SecuredAction.async { implicit request =>
      DeliveryAddressForm.form.bindFromRequest.fold(
        (hasErrors: Form[DeliveryAddressForm.Data]) => Future.successful(BadRequest(views.html.createDeliveryAddress(hasErrors, userId))),
        (success: DeliveryAddressForm.Data) => deliveryAddressDao.create(success, userId).map(_ => Redirect(routes.DeliveryAddressController.list(Some(userId))))
      )
  }

  def deliveryAddressCreateGet(userId: Int) = silhouette.SecuredAction {implicit request =>
    if (request.identity.id == Some(userId)) {
      Ok(views.html.createDeliveryAddress(DeliveryAddressForm.form, userId))
    } else {
      Redirect(routes.DeliveryAddressController.list(Some(userId)))
    }
  }

  def list(userId: Option[Int]) = silhouette.SecuredAction.async { implicit request =>
    userId match {
      case Some(userId) => deliveryAddressDao.findByUserId(userId).map { case (deliveryAddresses) => Ok(views.html.listOfDeliveryAddresses(deliveryAddresses, userId)) }
      case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
    }
  }

  val Home = Redirect(routes.Application.index())

  def deliveryAddressPostUpdate(id: Int) = silhouette.SecuredAction.async { implicit request =>
    DeliveryAddressForm.form.bindFromRequest.fold(
      (hasErrors: Form[DeliveryAddressForm.Data]) => Future.successful(BadRequest(views.html.editDeliveryAddress(hasErrors, id))),

      (success: DeliveryAddressForm.Data) => {
        for {
        _ <- deliveryAddressDao.update(id, success)
      } yield Home.flashing("success" -> "delivery address has been updated")
    }
    )
  }

  def deliveryAddressGetUpdate(id: Option[Int], userId: Int) = silhouette.SecuredAction.async { implicit request =>

    id match {
        case Some(id: Int) => {
          deliveryAddressDao.findById(id).map {
            case Some(deliveryAddress) => {

              if (request.identity.id != Some(userId)) {
                Redirect(routes.DeliveryAddressController.list(Some(userId)))
              } else {
                val formData: DeliveryAddressForm.Data = DeliveryAddressForm.Data(deliveryAddress.street, deliveryAddress.city, deliveryAddress.postalcode, deliveryAddress.country)
                Ok(views.html.editDeliveryAddress(DeliveryAddressForm.form.fill(formData), id))
              }
            }
            case None => NotFound
          }
        }
      }
  }
}
