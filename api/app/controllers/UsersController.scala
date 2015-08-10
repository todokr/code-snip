package controllers

import play.api.mvc._
import models.User
import jp.co.bizreach.elasticsearch4s._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object UsersController extends Controller {

  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"
  implicit val rds = (
    (__ \ 'accountName).read[String] and
      (__ \ 'email).read[String] and
      (__ \ 'interests).read[String] and
    (__ \ 'password).read[String]
    ) tupled

  // =====================================================================
  //                                                               actions
  //                                                               =======
  def create = Action(parse.json) { implicit rs =>
    ESClient.init()
    val result = rs.body.validate[(String, String, String, String)].map {
      case (name, mail, interest, pass) =>
        ESClient.using(url) { client =>
          client.insert(config, User.create(accountName = name, email = mail, interests = interest, password = pass))
        }
      case _ => None
    }
    ESClient.shutdown()
    Ok(Json.obj("result" -> "success")) // TODO ユーザー作成ができなかったときにNGにする
  }

  def show(id: String) = Action { implicit rs =>
    ESClient.init()
    val userData = ESClient.using(url) { client =>
      selectUserById(id) match {
        case None => Json.obj("result" -> "notFound")
        case Some(u) => Json.obj(
            "result" -> "success",
            "accountName" -> u.accountName,
            "email" -> u.email,
            "interests" -> u.interests // TODO 配列を処理できるように
        )
      }
    }
    ESClient.shutdown()
    Ok(userData.toString).as(JSON) // TODO toStringは明らかにおかしい
  }

  def showByEmail(mail: String) = Action { implicit rs =>
    ESClient.init()
    val userData = ESClient.using(url) { client =>
      selectUserByMail(mail) match {
        case None => Json.obj("result" -> "notFound")
        case Some(id) => Json.obj(
          "result" -> "success",
          "id" -> id
        )
      }
    }
    ESClient.shutdown()
    Ok(userData.toString).as(JSON) // TODO toStringは明らかにおかしい
  }

  def update(id: String) = Action(parse.json) { implicit rs =>
    ESClient.init()
    val result = selectUserById(id) match {
      case None => None
      case Some(u) => {
        rs.body.validate[(String, String, String, String)].map {
          case (name, mail, interest, pass) =>
            ESClient.using(url) { client =>
              client.update(config, id, User(accountName = name, email = mail, interests = interest, password = pass))
            }
          case _ => None
        }

      }
    }
    ESClient.shutdown()
    result match {
      case None => NotFound(Json.obj("result" -> "notFound")) // エラーの場合
      case _ => Ok(Json.obj("result" -> "success"))
    }
  }

  def delete(id: String) = Action { implicit rs =>
    ESClient.init()
    val result = ESClient.using(url) { client =>
      client.delete(config, id)
    }
    ESClient.shutdown()
    result match {
      case Right(map) => Ok(Json.obj("result" -> "success"))
      case Left => NotFound(Json.obj("result" -> "notFound")) // エラーの場合
    }
  }

  // ============================================================================
  //                                                                       helper
  //                                                                       ======
  @deprecated
  protected def selectUserById(id: String): Option[User] = {
    ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("_id", id))
      }.map(_._2)
    }
  }

  @deprecated
  protected def selectUserByMail(mail: String): Option[String] = {
    ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("email", mail))
      }.map(_._1)
    }
  }

  @deprecated
  protected def selectUserByName(name: String): Option[String] = {
    ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("accountName", name))
      }.map(_._1)
    }
  }
}

