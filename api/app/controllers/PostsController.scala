package controllers

import auth.AuthAction
import models._
import models.User._
import models.Post._
import play.api.libs.json.{Json, _}
import play.api.mvc._

/**
 * @author Shunsuke Tadokoro
 */
object PostsController extends Controller{

  implicit val postFormat = Json.format[Post]
  implicit val userFormat = Json.format[User]
  implicit val fromVPFormat = Json.format[FromViewPost]
  implicit val shownPFormat = Json.format[ShownPost]

  // 自分の投稿一覧
  def list = AuthAction { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("")
    val posts = selectPostListByUserId(uid);
    Ok(Json.toJson(posts))
  }

  // 自分がフォローしているユーザー+自分の投稿一覧
  def followList = AuthAction { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("")
    val posts = selectTimeline(uid)
    Ok(Json.toJson(posts))
  }

  // 投稿の新規作成
  def create = AuthAction(parse.json) { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("-1")
    rs.body.validate[FromViewPost].map { post =>
      insertPost(uid, post)
    } match {
      case JsError(_) => BadRequest(Json.obj("result" -> "failed"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  // 投稿の削除
  def delete(postId: String) = AuthAction { implicit rs =>
    deletePost(postId) match {
      case Right(map) => Ok(Json.obj("result" -> "success"))
      case Left(_) => NotFound(Json.obj("result" -> "notFound"))
    }
  }
}
