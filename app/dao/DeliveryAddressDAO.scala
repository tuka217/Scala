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

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Long]("USERID")
    def firstName = column[String]("FIRSTNAME")
    def lastName = column[String]("LASTNAME")
    def city = column[String]("CITY")
    def country = column[String]("COUNTRY")
    def street = column[String]("STREET")
    def postalcode = column[Int]("POSTALCODE")
    def user = foreignKey("delivery_addresses_fk_user_id", userId, users)(_.id)


    def * = (id, firstName, lastName, street, city, postalcode, country, userId) <> (DeliveryAddress.tupled, DeliveryAddress.unapply)
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

  def insert(deliveryAddress: DeliveryAddress): Future[Unit] = {
    db.run(deliveryAddresses += deliveryAddress).map(_ => ())
  }

  def update(id: Long, deliveryAddress: DeliveryAddress): Future[Unit] = {
//    val deliveryAddressToUpdate: DeliveryAddress = deliveryAddress.copy(id)
//    db.run(deliveryAddresses.filter(_.id === id).update(deliveryAddressToUpdate)).map(_ => ())

    val q = for { da <- deliveryAddresses if da.id === id } yield (da.firstName, da.lastName, da.city, da.country, da.street, da.postalcode)
    val updateAction = q.update(deliveryAddress.firstName, deliveryAddress.lastName, deliveryAddress.city, deliveryAddress.country, deliveryAddress.street, deliveryAddress.postalcode)
    db.run(updateAction).map(_ => ())
  }

  def findById(id: Long): Future[Option[DeliveryAddress]] = {
    db.run(deliveryAddresses.filter(_.id === id).result.headOption)
  }
}
