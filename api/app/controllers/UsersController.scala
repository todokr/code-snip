package controllers

import auth.AuthAction
import jp.co.bizreach.elasticsearch4s._
import models.{Follow, DisplayUser, User}
import models.User._
import play.Logger
import play.api.libs.json._
import play.api.mvc._
import play.api.libs.Crypto._

/**
 * @author shunsuke tadokoro
 */

object UsersController extends Controller {

  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"
  implicit val userFormat = Json.format[User]
  implicit val iuFormat = Json.format[DisplayUser]

  // ユーザーの作成
  def create = Action(parse.json) { implicit rs =>
    val email = (rs.body \ "email").as[String]
    if(selectUserByEmail(email).isDefined) {
      BadRequest(Json.obj("result" -> "exist"))
    } else {
      rs.body.validate[User].map {
        case x:User =>
          insertUser(x)
        case _ => None
      } match {
        case JsSuccess(_,_) => Ok(Json.obj("result" -> "success"))
        case _ => BadRequest(Json.obj("result" -> "failed"))
      }
    }
  }

  // ユーザーの詳細
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

  // ユーザー情報の更新
  def update = Action(parse.json) { implicit rs =>
    val id = selectUserBySession(rs).map(u => u._1).getOrElse("-1")
    selectUserById(id).map( u => {
      rs.body.validate[User].map {
        case newUser: User => updateUser(id, newUser, u._2)
        case _ => None
      }
    }) match {
      case None => NotFound(Json.obj("result" -> "notFound"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  // ユーザーの削除
  def delete(id: String) = Action { implicit rs =>
    val result = ESClient.using(url) { client =>
      client.delete(config, id)
    }
    result match {
      case Right(map) => Ok(Json.obj("result" -> "success"))
      case Left(_) => NotFound(Json.obj("result" -> "notFound"))
    }
  }

  // アカウント表示用データの取得
  def accountStatus = AuthAction { implicit rs =>
    selectUserBySession(rs) match {
      case Some(userData) => {
        val (id, user) = userData
        val (follow, follower) = FollowsController.selectFollowUsers(selectUserBySession(rs).get._1)
        Ok(Json.obj(
          "id"          -> id,
          "email"       -> user.email,
          "accountName" -> user.accountName,
          "imageUrl"    -> user.imageUrl,
          "interests"   -> user.interests,
          "follow"      -> follow.size,
          "follower"    -> follower.size
        ))
      }
      case None => BadRequest(Json.obj("result" -> "notAuthorized"))
    }
  }

  // 嗜好の近いユーザーリストの取得
  def listNearInterestUser = AuthAction { implicit rs =>
    val id =  selectUserBySession(rs) match {
      case Some(userData) => userData._1
      case None => ""
    }
    val followAndSelfList = Follow.selectFollowListByUserId(id) + id
    val nearUserList = selectUserListFromInterests(id).filterNot(recomendUser => followAndSelfList.contains(recomendUser.id))
    Ok(Json.toJson(nearUserList))
  }
}

