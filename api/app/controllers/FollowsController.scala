package controllers

import models.User
import play.Logger
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


  def follow = AuthAction(parse.json) { implicit rs =>
    val userId = selectUserBySession(rs).get._1
    val targetId = Json.stringify(rs.body \ "followToId").replaceAll("\"", "")
    if (userId == targetId) { BadRequest(Json.obj("result" -> "(・´з`・)"))}
    addFollow(userId, targetId) match {
      case Left => BadRequest(Json.obj("result" -> "failed"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  def unFollow = AuthAction(parse.json) { implicit rs =>
    val userId = selectUserBySession(rs).get._1
    val targetId = Json.stringify(rs.body \ "followToId").replaceAll("\"", "")
    removeFollow(userId, targetId) match {
      case Left => BadRequest(Json.obj("result" -> "failed"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  def listFollow = AuthAction { implicit rs =>
    val userId = selectUserBySession(rs).get._1
    val followUsers = selectFollowUsers(userId)
    Ok(Json.obj(
      "follow" -> followUsers._1,
      "follower" -> followUsers._2
    ))
  }

  def listFollower = AuthAction { implicit rs =>
    val result = selectFollowerListByUserId(selectUserBySession(rs).get._1)
    Ok(Json.obj("result" -> result.toString))
  }

  def selectFollowUsers(userId: String): (List[User], List[User]) = {
    val followUsers = selectFollowListByUserId(userId).map(uid => selectUserById(uid)).flatten.map(_._2)
    val followerUsers = selectFollowerListByUserId(userId).map(uid => selectUserById(uid)).flatten.map(_._2)
    (followUsers, followerUsers)
  }

  def selectFollowNumbers = AuthAction { implicit rs =>
    val (follow, follower) = selectFollowUsers(selectUserBySession(rs).get._1)
    Ok(Json.obj(
      "follow" -> follow.size,
      "follower" -> follower.size
    ))
  }
}
