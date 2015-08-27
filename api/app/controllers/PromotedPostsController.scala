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

  implicit val userFormat = Json.format[User]
  implicit val promoFormat = Json.format[PromotedPost]
  implicit val shownPromoFormat = Json.format[ShownPromotedPost]


  def showPromotedPost = AuthAction { implicit rs =>
    val uid = selectUserBySession(rs).map(u => u._1).getOrElse("")
    val promotedPost: List[ShownPromotedPost] = selectPromotedPost(uid)
    Ok(Json.toJson(promotedPost))
  }
}
