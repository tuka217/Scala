package dao

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.SignUpFormData
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import models._

class UserService @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                            passwordHasher: PasswordHasher,
                            authInfoRepository: AuthInfoRepository)
                           (implicit ec: ExecutionContext)
  extends IdentityService[User]
    with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run(
    userTable
      .filter(user => user.providerKey === loginInfo.providerKey && user.providerID === loginInfo.providerID)
      .result
      .headOption)

  def create(data: SignUpFormData.Data): Future[LoginInfo] = {

    val user = User(

      id = None,
      firstName = Some(data.firstName),
      lastName = Some(data.lastName),
      email = Some(data.email),
      providerID = CredentialsProvider.ID,
      providerKey = data.email)

    db.run {
      (userTable returning userTable.map(_.id)) += user
    } andThen {
      case Failure(_: Throwable) => None
      case Success(id: Option[Int]) => {
        val loginInfo: LoginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        val authInfo: PasswordInfo = passwordHasher.hash(data.password)
        authInfoRepository.add(loginInfo, authInfo)
      }
    } map { _id => LoginInfo(CredentialsProvider.ID, user.email.get) }

  }

  def all(): Future[Seq[User]] = db.run(userTable.result)

  def findByEmail(mail: String): Future[Option[User]] = {
    db.run(userTable.filter(_.email === mail).result).map(_.headOption)
  }
}

