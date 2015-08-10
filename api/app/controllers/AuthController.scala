package controllers

import play.Logger
import play.api.mvc.{DiscardingCookie, Cookie, Action, Controller}
import models.User._
import play.api.libs.json._
import play.api.libs.Crypto.sign
import play.api.libs.json.JsValue

/**
 * Created by shunsuke.tadokoro on 15/08/10.
 */

case class AuthArg(email: String, password: String)

object AuthController extends Controller{

  def login = Action(parse.json) { implicit rs =>
    val email = Json.stringify(rs.body \ "email").replaceAll("\"", "") // TODO \"が入ってしまうのを直す
    val pass = Json.stringify(rs.body \ "password").replaceAll("\"", "") // TODO \"が入ってしまうのを直す
    val user = findByEmail(email)
    user match {
      case Some(_) =>
        if (user.get.password == pass) {

          Ok(Json.obj("result" -> "success")).withSession("auth" -> user.get.email)
        } else {
          BadRequest(Json.obj("result" -> "invalid"))
        }
      case None => NotFound(Json.obj("result" -> "notFound"))
    }
  }

  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }
}
