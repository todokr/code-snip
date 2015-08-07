package models
import Users._

case class Users(
  userId: Option[String], 
  name: Option[String], 
  company: Option[object]
)

object Users {

  val typeName = "users"

  case class Company(
    companyId: Option[String], 
    name: Option[String]
  )

  implicit val reflectiveCalls = scala.language.reflectiveCalls
  implicit val implicitConversions = scala.language.implicitConversions

  implicit def name2string(name: Name): String = name.toString()

  case class Name(name: String){
    lazy val key = if(name.contains(".")) name.split("\\.").last else name
    override def toString(): String = name
  }
  val userId = Name("userId")
  val name = Name("name")
  val company = Name("company")

}