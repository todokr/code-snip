package controllers

import auth.AuthAction
import models.User._
import play.api.libs.json.Json
import play.api.mvc.Controller

/**
 * Created by shunsuke.tadokoro on 15/08/10.
 */
object TestController extends Controller{

  def main = AuthAction { implicit rs =>
    val email = getIdentifier(rs) // TODO 暫定的にEmailだが、この先どうするか考える
    Ok(Json.obj("hoge" -> "fuga", "email" -> email))
  }

  def logout = TODO
  def fail = TODO

}
