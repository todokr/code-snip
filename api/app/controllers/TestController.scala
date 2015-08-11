package controllers

import auth.AuthAction
import models.User._
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * @author shunsuke tadokoro
 */

object TestController extends Controller{

  def authNeed = AuthAction { implicit rs =>
    selectUserBySession(rs) match {
      case Some(userData) => {
        val id = userData._1
        val user = userData._2
        Logger.debug("id: " + id)
        Logger.debug("name: " + user.accountName)
        Ok(Json.obj(
          "result"      -> "authenticated",
          "id"          -> id,
          "email"       -> user.email,
          "accountName" -> user.accountName,
          "interests"   -> user.interests
        ))
      }
      case None => BadRequest(Json.obj("result" -> "notAuthorized"))
    }


  }

  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }

  def fail = TODO

}
