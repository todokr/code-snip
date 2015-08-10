package controllers

import auth.AuthAction
import models.User._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
 * Created by shunsuke.tadokoro on 15/08/10.
 */
object TestController extends Controller{

  def authNeed = AuthAction { implicit rs =>
    Ok(Json.obj(
      "hoge" -> "fuga",
      "identifier" -> getIdentifier(rs)
    ))
  }

  def logout = Action { implicit rs =>
    Ok(Json.obj("result" -> "logout")).withNewSession
  }

  def fail = TODO

}
