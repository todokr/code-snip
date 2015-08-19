package controllers

import auth.AuthAction
import jp.co.bizreach.elasticsearch4s.ESClient
import models.{ShownPost, User, Post}
import models.User._
import models.Post._
import models.Post.{config, url}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Json, _}
import play.api.mvc._

/**
 * @author Shunsuke Tadokoro
 */
object PostsController extends Controller{
  implicit val rds = (
    (__ \ 'code).read[String] and
    (__ \ 'description).read[String] and
    (__ \ 'tag).read[String]
  ) tupled

  implicit val postWrites = Json.writes[Post]
  implicit val userWrites = Json.writes[User]
  implicit val puWrites = Json.writes[ShownPost]


  // 自分の投稿一覧
  def list = AuthAction { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("")
    val posts = selectPostListByUserId(uid);
    Ok(Json.toJson(posts))
  }

  // 自分がフォローしているユーザーの投稿一覧
  def followList = AuthAction { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("")
    val posts = selectFollowPost(uid)
    Ok(Json.toJson(posts))
  }

  def create = AuthAction(parse.json) { implicit rs =>
    val uid = selectUserBySession(rs) match {
      case Some((id,_)) => id
      case None => ""
    }
    rs.body.validate[(String, String, String)].map {
      case (wroteCode, wroteDesc, tagName) =>
        ESClient.using(url) { client =>
          client.insert(config, Post(userId = uid, code = wroteCode, description = wroteDesc, tag = tagName, time = getCurrentDateTime))
        }
      case _ => JsError()
    } match {
      case JsError(_) => BadRequest(Json.obj("result" -> "failed"))
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  def show(id: String) = Action { implicit rs =>
    ESClient.using(url) { client =>
      selectPostById(id) match {
        case Some((id, post)) => {
          selectUserById(post.userId) match {
            case Some((userId, user)) => Ok(Json.toJson(Json.obj(
              "result"      -> "found",
              "id"          -> id,
              "accountName" -> user.accountName,
              "code"        -> post.code,
              "description" -> post.description,
              "tag"         -> post.tag
            ))).withHeaders("Access-Control-Allow-Origin" -> " *")

            case None => Ok(Json.toJson(Json.obj( // 投稿はあるがユーザーがいない場合
              "result"      -> "found",
              "id"          -> id,
              "accountName" -> "退会したユーザー",
              "code"        -> post.code,
              "description" -> post.description,
              "tag"         -> post.tag
            )))
          }
        }

        case None => BadRequest(Json.obj("result" -> "notFound"))
      }
    }
  }

  def delete(id: String) = AuthAction { implicit rs =>
    val result = ESClient.using(url) { client =>
      client.delete(config, id)
    }
    result match {
      case Right(map) => Ok(Json.obj("result" -> "success"))
      case Left(_) => NotFound(Json.obj("result" -> "notFound"))
    }
  }
}
