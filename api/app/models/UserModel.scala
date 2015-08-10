package models

import jp.co.bizreach.elasticsearch4s._
import play.Logger
import play.api.libs.json.JsValue

case class User(accountName: String, email: String, interests: String, password: String)

object User {
  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"

  def create(accountName: String, email: String, interests: String, password: String): User = {
    User(accountName, email, interests, password)
  }

  def findById(id: String): Option[User] = {
    ESClient.init()
    val user = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("_id", id))
      }.map(_._2)
    }
    ESClient.shutdown()
    user
  }

  def findByEmail(email: String): Option[User] = {
    ESClient.init()
    Logger.debug(email)
    val user = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("email", email))
      }.map(_._2)
    }
    ESClient.shutdown()
    user
  }
}
