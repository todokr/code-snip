package models

import jp.co.bizreach.elasticsearch4s._

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
}
