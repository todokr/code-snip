package models
import Companies._

case class Companies(
  companyId: Option[String], 
  name: Option[String]
)

object Companies {

  val typeName = "companies"


  implicit val reflectiveCalls = scala.language.reflectiveCalls
  implicit val implicitConversions = scala.language.implicitConversions

  implicit def name2string(name: Name): String = name.toString()

  case class Name(name: String){
    lazy val key = if(name.contains(".")) name.split("\\.").last else name
    override def toString(): String = name
  }
  val companyId = Name("companyId")
  val name = Name("name")

}