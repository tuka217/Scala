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

class Application @Inject() (
                              userDao: UsersDaoImpl,
                              messagesAction: MessagesActionBuilder,
                              controllerComponents: ControllerComponents
                            )(implicit executionContext: ExecutionContext) extends AbstractController(controllerComponents) with play.api.i18n.I18nSupport {

}
