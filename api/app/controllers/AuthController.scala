package controllers

import auth.AuthAction
import models.User
import models.User._
import play.api.libs.Crypto._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
 * @author shunsuke tadokoro
 */

case class AuthArg(email: String, password: String)

object AuthController extends Controller{

  implicit val userFormat = Json.format[User]

  def login = Action(parse.json) { implicit rs =>
    val email = Json.stringify(rs.body \ "email").replaceAll("\"", "") // TODO 無理矢理感あるからあとで直す
    val pass = Json.stringify(rs.body \ "password").replaceAll("\"", "") // TODO 無理矢理感あるからあとで直す

    val result = selectUserByEmail(email) match {
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
          )).withSession("auth" -> user.email)
        } else {
          BadRequest(Json.obj("result" -> "invalid"))
        }
      case None => BadRequest(Json.obj("result" -> "notExist"))
    }
    result
  }

  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }

  def authNeed = AuthAction { implicit rs =>
    selectUserBySession(rs) match {
      case Some(userData) => {
        Ok(Json.toJson(userData._2))
      }
      case None => BadRequest(Json.obj("result" -> "notAuthorized"))
    }
  }

}
