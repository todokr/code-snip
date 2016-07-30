package auth

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future

/**
 * @author shunsuke tadokoro
 */

object AuthAction extends ActionBuilder[Request]{

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = 
    request.session.get("auth") map (_ => block(request)) getOrElse Future.successful(Unauthorized)
  }
}
