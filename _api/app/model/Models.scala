package models

import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime

import play.api.data.Forms._
import play.api.data.{Form, FormError}
import play.api.data.format.Formatter
import play.api.libs.Crypto._
import play.api.Logger

case class User(userId: String, accountName: String, email: String, interests: String, cryptedPassword: String)

object User {
  def create(accountName: String, email: String, interests: String, password: String): User = {
    val id = "u" + ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
    val cryptedPass = sign(password)
    User(id, accountName, email, interests, cryptedPass)
  }
}

/**
 * QueryStringの値を取得したり、フォームから値を取得したりする場合に case class + Form で実装する 
 */
case class QueryCondition(
                           size: Int = QueryCondition.defaultLimit,
                           from: Int = QueryCondition.defaultOffset,
                           params: Map[String, String] = Map()
                           )

object QueryCondition {
  val defaultLimit = 20
  val defaultOffset = 0

  private val mapFormat: Formatter[Map[String, String]] =
    new Formatter[Map[String, String]] {
      def bind(key: String, data: Map[String, String]) = {
        Some(data - "limit" - "offset")
          .toRight(Seq(FormError(key, "error.required", Nil)))
      }

      def unbind(key: String, value: Map[String, String]) =
        value // Dummy implementation
    }


  val form = Form(
    mapping(
      "size"     -> default(number(min = 1, max = 100), defaultLimit),
      "from"    -> default(number(min = 0), defaultOffset),
      "params"   -> of[Map[String, String]](mapFormat)
    )(QueryCondition.apply)(QueryCondition.unapply)
  )
}