package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import models._
import util._
import scala.concurrent.ExecutionContext
import scala.concurrent._
import javax.inject.Inject

trait BaseController extends play.api.mvc.BaseController with  play.api.i18n.I18nSupport {

  val  executionContext: ExecutionContext

  def authenticated(f: AuthenticatedRequest => Result) = {
    Action { request =>
      val session = request.session
      UserObject.retrieveUserFromSession(session) match {
        case Some(userInfo: User) =>
         f(AuthenticatedRequest(userInfo, request))
        case None =>
          Redirect(routes.Application.login()).withSession(session)
      }
    }
  }

  def authenticatedAsync(f: AuthenticatedRequest => Result) = {
    Action.async { request =>
      val session = request.session
      UserObject.retrieveUserFromSession(session) match {
        case Some(userInfo: User) =>
          Future{f(AuthenticatedRequest(userInfo, request)) }(executionContext)
        case None =>
          Future{Redirect(routes.Application.login()).withSession(session) }(executionContext)
      }
    }
  }

  case class AuthenticatedRequest (val userInfo: User, request: Request[AnyContent]) extends WrappedRequest(request)
}
