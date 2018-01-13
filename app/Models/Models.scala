package models

import play.api.libs.json.{Json,Format}
import Json._
import play.api.mvc.{Session,Request,AnyContent}


case class DeliveryAddress (id: Long, firstName: String, lastName: String, street: String, city: String, postalcode: Int, country: String, userId: Long)
case class User(username: String, email: String, password: String, id: Long)

object UserObject {

  implicit val userFormat = format[User]

//  def isAuthenticated(session: Session): Boolean = {
//    session.get("fbAccessToken") match {
//      case Some(token) =>
//        val expirationTS = (session.get("fbTokenExpires").getOrElse("0").toLong * 1000)
//        System.currentTimeMillis < expirationTS
//      case None =>
//        false
//    }
//  }

  def serializeUser(user: User): String = {
    stringify(toJson(user))
  }

  def deserializeUser(userJson: String): User = {
    parse(userJson).as[User]
  }

//  def retrieveAccessTokenFromSession(session: Session): Option[String] = {
//    if(isAuthenticated(session))
//      session.get("accessToken")
//    else
//      None
//  }

  def retrieveUserFromSession(session: Session): Option[User] = {
      session.get("user") match {
        case Some(userJson) => Some(deserializeUser(userJson))
        case None => None
      }
  }

//  def retrieveAccessToken(state: String, code: String): Option[(String,Long)] = {
//    val fbAccessRequest = url("https://graph.facebook.com/oauth/access_token") <<? Map (
//      "client_id" -> appId, "redirect_uri" -> redirectUrl, "client_secret" -> appSecret, "code" -> code
//    )
//
//    val fbAccessResponse = Http(fbAccessRequest OK as.String)
//    Await.result(fbAccessResponse.map(tryExtractTokenFromResponse), 5 seconds)
//  }

//  def tryExtractTokenFromResponse(resp: String): Option[(String,Long)] = {
//    try {
//      val tokenExtractor = new Regex("access_token=(.*?)&expires=(.*)")
//      val tokenExtractor(token: String, expires: String) = resp
//      val expireTimestamp = (expires.toLong * 1000) + System.currentTimeMillis
//      Some((token, expireTimestamp))
//    } catch {
//      case e: MatchError => None
//    }
//  }

//  def retrieveFacebookUser(accessToken: String): FacebookUser = {
//    val fbDataRequest = url("https://graph.facebook.com/me") <<? Map("access_token" -> accessToken)
//    val fbDataResponse = Http(fbDataRequest OK as.String)
//    Await.result(fbDataResponse.map(deserializeUser), 5 seconds)
//  }
}