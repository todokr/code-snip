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

  // ログイン
  def login = Action(parse.json) { implicit rs =>
    val email = (rs.body \ "email").as[String]
    val pass = (rs.body \ "password").as[String]

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
          )).withSession("auth" -> user.email)
        } else {
          BadRequest(Json.obj("result" -> "invalid"))
        }
      case None => BadRequest(Json.obj("result" -> "notExist"))
    }
  }

  // ログアウト
  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }

  // 認証確認
  def authNeed = AuthAction { implicit rs =>
    selectUserBySession(rs) match {
      case Some(userData) => {
        Ok(Json.toJson(userData._2))
      }
      case None => BadRequest(Json.obj("result" -> "notAuthorized"))
    }
  }
}
