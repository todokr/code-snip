package controllers

import auth.AuthAction
import models._
import play.api.libs.json.Json
import play.api.mvc.Controller

/**
 * @author Shunsuke Tadokoro
 */
object FavoritesController extends Controller{

  implicit val favFormat       = Json.format[Favorite]
  implicit val userFormat      = Json.format[User]
  implicit val postFormat      = Json.format[Post]
  implicit val shownFavFormat  = Json.format[ShownFavorite]
  implicit val shownPostFormat = Json.format[ShownPost]

  // お気に入りリスト
  def list = AuthAction { implicit rs =>
    User.selectUserBySession(rs).map { case (userId, _) =>
        Ok(Json.toJson(Favorite.selectFavoriteList(userId)))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // お気に入り追加
  def addFavorite = AuthAction(parse.json) { implicit rs =>
    val postId = (rs.body \ "favoritePostId").as[String]

    User.selectUserBySession(rs).map { case (userId, _) =>
      Favorite.insertFavorite(userId, postId) match {
        case Right(_) => Ok(Json.obj("result" -> "success"))
        case _        => BadRequest(Json.obj("result" -> "failed"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }

  // お気に入り削除
  def removeFavorite = AuthAction(parse.json) { implicit rs =>
    val targetPostId = (rs.body \ "favoritePostId").as[String]
    User.selectUserBySession(rs).map { case (userId, _) =>
      Favorite.removeFavorite(userId, targetPostId) match {
        case Right(_) => Ok(Json.obj("result" -> "success"))
        case _        => BadRequest(Json.obj("result" -> "failed"))
      }
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }
}
