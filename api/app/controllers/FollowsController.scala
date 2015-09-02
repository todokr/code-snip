package controllers

import models.{Follow, DisplayUser, User}
import play.api.mvc._
import play.api.libs.json._
import auth.AuthAction
import models.User._
import models.Follow._


/**
 * @author Shunsuke Tadokoro
 */
object FollowsController extends Controller {

  implicit val userFormats = Json.format[User]
  implicit val iwuFormats  = Json.format[DisplayUser]

  // フォロー追加
  def follow = AuthAction(parse.json) { implicit rs =>
    val targetId = (rs.body \ "followToId").as[String]
    selectUserBySession(rs).map(u => u._1).map { userId =>
      if (userId == targetId) { BadRequest(Json.obj("result" -> "cannotFollowYourself")) }
      addFollow(userId, targetId) match {
        case Right(_) => Ok(Json.obj("result" -> "success"))
        case _        => BadRequest(Json.obj("result" -> "failed"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // フォロー解除
  def unFollow = AuthAction(parse.json) { implicit rs =>
    val targetId = (rs.body \ "followToId").as[String]
    selectUserBySession(rs).map(u => u._1).map { userId =>
      removeFollow(userId, targetId) match {
        case Right(_) => Ok(Json.obj("result" -> "success"))
        case _        => BadRequest(Json.obj("result" -> "failed"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // フォロー一覧
  def listFollowUsers = AuthAction { implicit rs =>
    selectUserBySession(rs).map(u => u._1).map { userId =>
      val result = selectFollowListByUserId(userId).map(uid => selectUserById(uid)).flatten.map(u => DisplayUser(u._1, u._2, true))
      Ok(Json.toJson(result))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // フォロワー一覧
  def listFollower = AuthAction { implicit rs =>
    selectUserBySession(rs).map(u => u._1).map { userId =>
      val followList = selectFollowListByUserId(userId)
      val result = selectFollowerListByUserId(userId).map(followerId => {
        val isFollowing = followList.contains(followerId)
        selectUserById(followerId).map(follower => {
          DisplayUser(follower._1, follower._2, isFollowing)
        })
      })
      Ok(Json.toJson(result))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // フォローとフォロワーを一度に取得
  def selectFollowUsers(userId: String): (List[User], List[User]) = {
    val followUsers = selectFollowListByUserId(userId).map(uid => selectUserById(uid)).flatten.map(_._2)
    val followerUsers = selectFollowerListByUserId(userId).map(uid => selectUserById(uid)).flatten.map(_._2)
    (followUsers, followerUsers)
  }

  // フォローとフォロワーの数を一度に取得
  def selectFollowNumbers = AuthAction { implicit rs =>
    selectUserBySession(rs).map(u => u._1).map { userId =>
      val (follow, follower) = selectFollowUsers(userId)
      Ok(Json.obj(
        "follow"   -> follow.size,
        "follower" -> follower.size
      ))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }
}
