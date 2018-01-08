package models

case class DeliveryAddress (id: Long, firstName: String, lastName: String, street: String, city: String, postalcode: Int, country: String, userId: Long)
case class User(username: String, email: String, password: String, id: Long)
