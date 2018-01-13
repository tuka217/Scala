package dao

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait UsersComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  class UsersTable(tag: Tag) extends Table[User](tag, "USER") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def username = column[String]("USERNAME")
    def email = column[String]("EMAIL")
    def password = column[String]("PASSWORD")

    def * = (username,email,password,id) <> (User.tupled, User.unapply)
  }

}


class UsersDaoImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) (implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with UsersComponent {

  import driver.api._

  private val Users = TableQuery[UsersTable]

  def all(): Future[Seq[User]] = db.run(Users.result)

  def insert(user: User): Future[Unit] = db.run(Users += user).map { _ => () }


  def findByEmail(mail: String): Future[Option[User]] = {
    db.run(Users.filter(_.email === mail).result).map(_.headOption)
  }

  def findByUsername(usernm: String): Future[Option[User]] = {
    db.run(Users.filter(_.username === usernm).result).map(_.headOption)
  }

  def findByEmailAndPassword(mail: String, pass: String) = {
    db.run(Users.filter(_.email === mail).filter(_.password === pass).result).map(_.headOption)
  }

}
