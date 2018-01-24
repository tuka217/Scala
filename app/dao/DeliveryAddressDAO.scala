package dao

import javax.inject.Inject

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models._
import scala.util.Failure
import scala.util.Success
import scala.concurrent.{ExecutionContext, Future}
import forms.DeliveryAddressForm

class DeliveryAddressDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  def create(data: DeliveryAddressForm.Data, newUserId: Int): Future[Unit] = {

    val deliveryAddress: DeliveryAddress = DeliveryAddress(
      id = None,
      street = data.street,
      city = data.city,
      postalcode = data.postalcode,
      country = data.country,
      userId = Some(newUserId),
    )

    db.run(deliveryAddressTable +=deliveryAddress).map { _ => () }
  }

  def update(id: Int, data: DeliveryAddressForm.Data): Future[Unit] = {

    val deliveryAddress: DeliveryAddress = DeliveryAddress(
      id = None,
      street = data.street,
      city = data.city,
      postalcode = data.postalcode,
      country = data.country,
      userId = None
    )

    val q = for { c <- deliveryAddressTable if c.id === id } yield (c.street, c.city, c.postalcode, c.country)
    db.run(q.update(data.street, data.city, data.postalcode, data.country)).map(_ => ())

  }

  def all(): Future[Seq[DeliveryAddress]] = db.run(deliveryAddressTable.result)

  def findById(id: Int): Future[Option[DeliveryAddress]] = {
    db.run(deliveryAddressTable.filter(_.id === id).result).map(_.headOption)
  }

  def findByUserId(id: Int): Future[Seq[DeliveryAddress]] = {
    db.run(deliveryAddressTable.filter(_.userId === id).result)
  }
}
