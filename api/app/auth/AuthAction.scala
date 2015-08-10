package auth

/**
 * Created by shunsuke.tadokoro on 15/08/10.
 */

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Results, Result, ActionBuilder, Request}
import scala.concurrent.Future

object AuthAction extends ActionBuilder[Request]{

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    Logger.debug("beforeAction is invoked.")
    request.session.get("auth") match {
      case Some(_) => block.apply(request)
      case None => Future.successful(Results.Status(401).apply(Json.obj("result" -> "notAuthorized")))
    }
  }
}
