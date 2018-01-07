package dao

import scala.concurrent.{ ExecutionContext, Future }
import javax.inject.Inject

import models.DeliveryAddress
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait DeliveryAddressesComponent
  extends UsersComponent
{ self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  val users = TableQuery[UsersTable]

  class DeliveryAddressesTable(tag: Tag) extends Table[DeliveryAddress](tag, "DELIVERYADDRESS") {

    val users = TableQuery[UsersTable]

    def userId = column[Long]("USERID")
    def firstName = column[String]("FIRSTNAME")
    def lastName = column[String]("LASTNAME")
    def city = column[String]("CITY")
    def country = column[String]("COUNTRY")
    def street = column[String]("STREET")
    def postalcode = column[Int]("POSTALCODE")
    def user = foreignKey("delivery_addresses_fk_user_id", userId, users)(_.id)


    def * = (firstName, lastName, street, city, postalcode, country, userId) <> (DeliveryAddress.tupled, DeliveryAddress.unapply)
  }
}

class DeliveryAddressDAO  @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile]
  with DeliveryAddressesComponent {

  import profile.api._

  private val deliveryAddresses = TableQuery[DeliveryAddressesTable]

  def all(): Future[Seq[DeliveryAddress]] = db.run(deliveryAddresses.result)

  def findByUserId(userId: Long): Future[Seq[DeliveryAddress]] = {
    db.run(deliveryAddresses.filter(_.userId === userId).result)
  }

  def insert(deliveryAddress: DeliveryAddress): Future[Unit] = db.run(deliveryAddresses += deliveryAddress).map { _ => () }
}
