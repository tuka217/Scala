
import com.mohiva.play.silhouette.api.Identity
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import slick.lifted.ProvenShape.proveShapeOf

package object models {

  case class DeliveryAddress(id: Option[Int], street: String, city: String, postalcode: Int, country: String, userId: Option[Int])

  class DeliveryAddresses(tag: Tag) extends Table[DeliveryAddress](tag, "DELIVERYADDRESS") {

    def id = column[Option[Int]]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Option[Int]]("USERID")
    def city = column[String]("CITY")
    def country = column[String]("COUNTRY")
    def street = column[String]("STREET")
    def postalcode = column[Int]("POSTALCODE")
    def user = foreignKey("delivery_addresses_fk_user_id", userId, userTable)(_.id)

    def * = (id, street, city, postalcode, country, userId) <> (DeliveryAddress.tupled, DeliveryAddress.unapply)
  }

  case class User(id: Option[Int],
                  firstName: Option[String],
                  lastName: Option[String],
                  email: Option[String],
                  providerID: String,
                  providerKey: String) extends Identity

  class Users(tag: Tag) extends Table[User](tag, "USER") {
    def id = column[Option[Int]]("USER_ID", O.PrimaryKey, O.AutoInc)

    def firstName = column[Option[String]]("FIRST_NAME")

    def lastName = column[Option[String]]("LAST_NAME")

    def email = column[Option[String]]("EMAIL")

    def providerID = column[String]("PROVIDER_ID")

    def providerKey = column[String]("PROVIDER_KEY")

    def * : ProvenShape[User] = (id, firstName, lastName, email, providerID, providerKey) <> (User.tupled, User.unapply)
  }

  case class Password(key: String,
                      hasher: String,
                      hash: String,
                      salt: Option[String])

  class Passwords(tag: Tag) extends Table[Password](tag, "PASSWORD") {
    def key = column[String]("PROVIDER_KEY", O.PrimaryKey)

    def hasher = column[String]("HASHER")

    def hash = column[String]("HASH")

    def salt = column[Option[String]]("SALT")

    def * = (key, hasher, hash, salt) <> (Password.tupled, Password.unapply)
  }

  val userTable = TableQuery[Users]
  val passwordTable = TableQuery[Passwords]
  val deliveryAddressTable = TableQuery[DeliveryAddresses]
}
