package controllers

import jp.co.bizreach.elasticsearch4s._
import models.User
import models.User._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

/**
 * @author shunsuke tadokoro
 */

object UsersController extends Controller {

  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"
  implicit val rds = (
    (__ \ 'accountName).read[String] and
      (__ \ 'email).read[String](Reads.email) and
      (__ \ 'interests).read[Seq[String]] and
    (__ \ 'password).read[String]
    ) tupled

  // =====================================================================
  //                                                               actions
  //                                                               =======
  def create = Action(parse.json) { implicit rs =>
    val email = Json.stringify(rs.body \ "email").replaceAll("\"", "")
    if(selectUserByEmail(email).isDefined) {
      BadRequest(Json.obj("result" -> "exist"))
    } else {
      rs.body.validate[(String, String, Seq[String], String)].map {
        case (name, mail, interest, pass) =>
          ESClient.using(url) { client =>
            client.insert(config, User.create(accountName = name, email = mail, interests = interest, password = pass))
          }
        case _ => None
      }
      Ok(Json.obj("result" -> "success"))
    }
  }

  def show(id: String) = Action { implicit rs =>
    ESClient.using(url) { client =>
      selectUserById(id) match {
        case Some((id, user)) => Ok(Json.toJson(Json.obj(
          "result"      -> "found",
          "id"          -> id,
          "accountName" -> user.accountName,
          "email"       -> user.email,
          "interest"    -> user.interests
        )))
        case None => BadRequest(Json.toJson(Json.obj("result" -> "notFound")))
      }
    }

  }

  def update(id: String) = Action(parse.json) { implicit rs =>
    val result = selectUserById(id) match {
      case None => None
      case Some(u) => {
        ESClient.init()
        rs.body.validate[(String, String, Seq[String], String)].map {
          case (name, mail, interest, pass) =>
            ESClient.using(url) { client =>
              client.update(config, id, User.create(accountName = name, email = mail, interests = interest, password = pass))
            }
          case _ => None
        }
        ESClient.shutdown()
      }
    }
    result match {
      case None => NotFound(Json.obj("result" -> "notFound"))
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
      case Left(_) => NotFound(Json.obj("result" -> "notFound"))
    }
  }

}

