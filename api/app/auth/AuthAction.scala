package auth

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future

/**
 * @author shunsuke tadokoro
 */

object AuthAction extends ActionBuilder[Request]{

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    Logger.debug("beforeAction is invoked.")
    request.session.get("auth") match {
      case Some(_) => block.apply(request)
      case None => {
        Logger.error("not authorized access tried")
        Future.successful(Results.Status(401).apply(Json.obj("result" -> "Not Authorized")))
      }
    }
  }
}
