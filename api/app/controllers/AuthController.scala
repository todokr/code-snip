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
    selectUserByEmail(email) match {
      case Some(userData) =>
        if(userData._2.password == sign(pass)) {
          val id = userData._1
          val user = userData._2
          Ok(Json.obj(
            "result" -> "loggedin",
            "id"     -> id,
            "accountName" -> user.accountName,
            "email" -> user.email,
            "interests" -> user.interests
          ))
        } else {
          BadRequest(Json.obj("result" -> "invalid"))
        }
      case None => BadRequest(Json.obj("result" -> "notExist"))
    }
  }

  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }
}
