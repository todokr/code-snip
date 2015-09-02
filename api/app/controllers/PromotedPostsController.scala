package controllers

import auth.AuthAction
import models.{User, ShownPromotedPost, PromotedPost}
import models.User._
import models.PromotedPost._
import play.api.libs.json.{Json, _}
import play.api.mvc.Controller

/**
 * @author Shunsuke Tadokoro
 */
object PromotedPostsController extends Controller {

  implicit val userFormat       = Json.format[User]
  implicit val promoFormat      = Json.format[PromotedPost]
  implicit val shownPromoFormat = Json.format[ShownPromotedPost]


  def showPromotedPost = AuthAction { implicit rs =>
    selectUserBySession(rs).map(u => u._1).map { userId =>
      val promotedPost: List[ShownPromotedPost] = selectPromotedPost(userId)
      Ok(Json.toJson(promotedPost))
    }.getOrElse(NotFound(Json.obj("result" -> "notFound")))
  }
}
