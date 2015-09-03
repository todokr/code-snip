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

  implicit val postFormat   = Json.format[Post]
  implicit val userFormat   = Json.format[User]
  implicit val fromVPFormat = Json.format[FromViewPost]
  implicit val shownPFormat = Json.format[ShownPost]

  // 自分の投稿一覧
  def list = AuthAction { implicit rs =>
    selectUserBySession(rs).map { case (userId, _) =>
      val posts = selectPostListByUserId(userId)
      Ok(Json.toJson(posts))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // 自分がフォローしているユーザー+自分の投稿一覧
  def followList = AuthAction { implicit rs =>
    selectUserBySession(rs).map { case (userId, _) =>
      val posts = selectTimeline(userId)
      Ok(Json.toJson(posts))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // 投稿の新規作成
  def create = AuthAction(parse.json) { implicit rs =>
    selectUserBySession(rs).map { case (userId, _) =>
      rs.body.validate[FromViewPost].map { post =>
        insertPost(userId, post)
      } match {
        case JsSuccess(_, _) => Ok(Json.obj("result" -> "success"))
        case _               => BadRequest(Json.obj("result" -> "failed"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // 投稿の削除
  def delete(postId: String) = AuthAction { implicit rs =>
    deletePost(postId) match {
      case Right(_) => Ok(Json.obj("result" -> "success"))
      case _        => NotFound(Json.obj("result" -> "notFound"))
    }
  }
}
