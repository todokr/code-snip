package controllers

/**
 * Created by shunsuke.tadokoro on 15/08/06.
 */

import play.api.mvc._
import models.User
import play.api.libs.json._
import jp.co.bizreach.elasticsearch4s._
import play.api.Logger

object UsersController extends Controller{

  def show = TODO

  def create = Action { implicit req =>

    ESClient.init()
    ESClient.using("http://localhost:9200") { client =>
      val config = ESConfig("code_snip", "user")
      client.insert(config, User.create(accountName = "testUser", email = "test@example.com", interests = "", password = "hogehoge"))
    }
    ESClient.shutdown()
    Ok(Json.obj("result" -> "successs"))
  }

  def update = {}

  def delete = {}

}
