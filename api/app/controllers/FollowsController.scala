package controllers

import models.User
import play.Logger
import play.api.libs.json.Json
import play.api.mvc.Controller

import auth.AuthAction
import models.User._
import models.Follow._

/**
 * @author Shunsuke Tadokoro
 */
object FollowsController extends Controller {

  implicit val userFormats = Json.format[User]

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
