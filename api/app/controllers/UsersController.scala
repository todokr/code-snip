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
  implicit val iuFormat   = Json.format[DisplayUser]

  // ユーザーの作成
  def create = Action(parse.json) { implicit rs =>
    val email = (rs.body \ "email").as[String]
    if(selectUserByEmail(email).isDefined) {
      BadRequest(Json.obj("result" -> "exist"))
    } else {
      rs.body.validate[User].map {
        case x:User => insertUser(x)
        case _      => None
      } match {
        case JsSuccess(_,_) => Ok(Json.obj("result" -> "success"))
        case _              => BadRequest(Json.obj("result" -> "failed"))
      }
    }
  }

  // ユーザー情報の更新
  def update = Action(parse.json) { implicit rs =>
    selectUserBySession(rs).map { case (userId, currentUser) =>
      rs.body.validate[User].map { newUser =>
        updateUser(userId, currentUser, newUser)
      } match {
        case JsSuccess(_, _) => Ok(Json.obj("result" -> "success"))
        case _ => NotFound(Json.obj("result" -> "notFound"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // アカウント表示用データの取得
  def accountStatus = AuthAction { implicit rs =>
    selectUserBySession(rs).map{ case (userId, user) =>
      val (follow, follower) = FollowsController.selectFollowUsers(userId)
      Ok(Json.obj(
        "id"          -> userId,
        "email"       -> user.email,
        "accountName" -> user.accountName,
        "imageUrl"    -> user.imageUrl,
        "interests"   -> user.interests,
        "follow"      -> follow.size,
        "follower"    -> follower.size
      ))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // 嗜好の近いユーザーリストの取得
  def listNearInterestUser = AuthAction { implicit rs =>
    selectUserBySession(rs).map { case (userId, _) =>
      val followAndSelfList = Follow.selectFollowerListByUserId(userId)
      val nearUserList = selectUserListFromInterests(userId).filterNot(recomUser => followAndSelfList.contains(recomUser.id))
      Ok(Json.toJson(nearUserList))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }
}

