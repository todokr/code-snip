package controllers

import auth.AuthAction
import jp.co.bizreach.elasticsearch4s._
import models.{IdWithUser, User}
import models.User._
import play.Logger
import play.api.libs.json._
import play.api.mvc._

/**
 * @author shunsuke tadokoro
 */

object UsersController extends Controller {

  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"
  implicit val userFormat = Json.format[User]
  implicit val iuFormat = Json.format[IdWithUser]

  // =====================================================================
  //                                                               actions
  //                                                               =======
  def create = Action(parse.json) { implicit rs =>
    val email = Json.stringify(rs.body \ "email").replaceAll("\"", "")
    if(selectUserByEmail(email).isDefined) {
      BadRequest(Json.obj("result" -> "exist"))
    } else {
      // 思い出 rs.body.validate[(String, String, Seq[String], String)].map {
      val hoge = rs.body.validate[User].map {
        case x:User =>
          ESClient.using(url) { client =>
            client.insert(config, User.setCrypted(accountName = x.accountName, email = x.email, interests = x.interests, password = x.password))
          }
        case _ => None
      }
      Ok(Json.obj("result" -> "success"))
    }
  }

  def show(id: String) = Action { implicit rs =>
    ESClient.using(url) { client =>
      selectUserById(id) match {
        case Some((uid, user)) => Ok(Json.toJson(Json.obj(
          "result"      -> "found",
          "id"          -> uid,
          "accountName" -> user.accountName,
          "email"       -> user.email,
          "interest"    -> user.interests
        )))
        case None => BadRequest(Json.toJson(Json.obj("result" -> "notFound")))
      }
    }
  }

  def update(id: String) = Action(parse.json) { implicit rs =>
    selectUserById(id).map( u => {
      rs.body.validate[User].map {
        case x: User =>
          ESClient.using(url) { client =>
            client.update(config, id, User.setCrypted(accountName = x.accountName, email = x.email, interests = x.interests, password = x.password))
          }
        case _ => None
      }
    }) match {
      case None => NotFound(Json.obj("result" -> "notFound"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  def delete(id: String) = Action { implicit rs =>
    val result = ESClient.using(url) { client =>
      client.delete(config, id)
    }
    result match {
      case Right(map) => Ok(Json.obj("result" -> "success"))
      case Left(_) => NotFound(Json.obj("result" -> "notFound"))
    }
  }

  def accountStatus = AuthAction { implicit rs =>
    selectUserBySession(rs) match {
      case Some(userData) => {
        val (id, user) = userData
        val (follow, follower) = FollowsController.selectFollowUsers(selectUserBySession(rs).get._1)
        Ok(Json.obj(
          "id"          -> id,
          "email"       -> user.email,
          "accountName" -> user.accountName,
          "interests"   -> user.interests,
          "follow"      -> follow.size,
          "follower"    -> follower.size
        ))
      }
      case None => BadRequest(Json.obj("result" -> "notAuthorized"))
    }
  }

  def listNearInterestUser = AuthAction { implicit rs =>
    val (id, interests:Seq[String]) =  selectUserBySession(rs) match {
      case Some(userData) => (userData._1, userData._2.interests)
      case None => None
    }
    val nearUserList = selectUserListFromInterests(interests).filterNot(user => user.id == id)
    nearUserList.foreach(s => println(s.toString))
    Ok(Json.toJson(nearUserList))

  }
}

