package controllers

import models.User
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.search.{SearchHit, SearchHits}
import org.elasticsearch.transport.RemoteTransportException
import play.api.mvc._

import play.api.libs.json._
import utils.ElasticsearchUtil

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import jp.co.bizreach.elasticsearch4s._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger



object UsersController extends Controller {

  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"
  implicit val rds = (
    (__ \ 'accountName).read[String] and
      (__ \ 'email).read[String] and
      (__ \ 'interests).read[String] and
    (__ \ 'password).read[String]
    ) tupled

  // OKとNGを分けたい
//  def create = Action(parse.json) { implicit rs =>
//    ESClient.init()
//
//    rs.body.validate[(String, String, String, String)].map {
//      case (name, mail, interest, pass) =>
//        ESClient.using(url) { client =>
//          client.insert(config, User.create(accountName = name, email = mail, interests = interest, password = pass))
//          ESClient.shutdown()
//          Ok(Json.obj("result" -> "success"))
//        }
//    }.recoverTotal{
//      ESClient.shutdown()
//      e => BadRequest("Detected error:"+ JsError.toFlatJson(e))
//    }
//  }

  def create = Action(parse.json) { implicit rs =>
    ESClient.init()
      rs.body.validate[(String, String, String, String)].map {
        case (name, mail, interest, pass) =>
          ESClient.using(url) { client =>
            client.insert(config, User.create(accountName = name, email = mail, interests = interest, password = pass))
          }
      }
    ESClient.shutdown()
    Ok(Json.obj("result" -> "success")) // TODO ユーザー作成ができなかったときにNGにする
  }


  def show(id: String) = Action { implicit rs =>
    ESClient.init()
    val userData = ESClient.using(url) { client =>

      val user: Option[User] = client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("userId", id))
      }.map(_._2)
      user match {
        case None => Json.obj("result" -> "notFound")
        case Some(u) => Json.obj(
            "result" -> "success",
            "userId" -> u.userId,
            "email" -> u.email,
            "interests" -> u.interests
        )
      }
    }
    ESClient.shutdown()
    Ok(userData.toString).as(JSON)
  }

  def show2 = Action { implicit rs =>
    ESClient.init()
    val userData = ESClient.using(url) { client =>
      val user: Option[User] = client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("userId", "u20150806220956394"))
      }.map(_._2)
      implicit val jsonWrites = Json.writes[User]
      user match {
        case None => Json.obj("result" -> "notFound")
        //case Some(u) => Json.toJson(u) // TODO ここで落ちる
      }
    }
    ESClient.shutdown()
    Ok(userData)
  }

  def update(id: String) = Action(parse.json) { implicit => rs
    ESClient.init()
    ESClient.using(url) { client =>
      client.update(config, "1", User(userId = "hoge", accountName = "hoge", email = "hoge@hoge.com", interests = "hoge", password = "hoge"))
    }
  }

  def delete(id: String) = Action { implicit rs =>
    ESClient.init()
    ESClient.using(url) { client =>
      client.delete(config, id)
    }
    ESClient.shutdown()
    Ok(Json.obj("result" -> "success")) // TODO 削除ができなかったときにNGにする
  }
}

