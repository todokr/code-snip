package controllers

import play.Logger
import play.api.mvc.{Action, Controller}
import models.User._
import play.api.libs.json._
import play.api.libs.Crypto.sign

/**
 * Created by shunsuke.tadokoro on 15/08/10.
 */

case class AuthArg(email: String, password: String)

object AuthController extends Controller{

  def login = Action(parse.json) { implicit rs =>
    val email = Json.stringify(rs.body \ "email").replaceAll("\"", "") // TODO 無理矢理感あるからあとで直す
    val pass = Json.stringify(rs.body \ "password").replaceAll("\"", "") // TODO 無理矢理感あるからあとで直す
    findByEmail(email) match {
      case Some(user) =>
        if (user.password == sign(pass)) {
          Ok(Json.obj("result" -> "success")).withSession("auth" -> user.email)
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
