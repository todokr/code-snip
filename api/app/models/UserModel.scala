package models

import jp.co.bizreach.elasticsearch4s._
import play.Logger
import play.api.libs.json.JsValue
import play.api.mvc.Request

case class User(accountName: String, email: String, interests: Seq[String], password: String)

object User {
  val config = ESConfig("code_snip", "user")
  val url = "http://localhost:9200"

  def create(accountName: String, email: String, interests: Seq[String], password: String): User = {
    User(accountName, email, interests, password)
  }

  /** idを受け取ってUserを検索した結果をOptionに詰めて返す
    * @param id Userのid (NotNull)
    * @return 検索結果
  */
  def findById(id: String): Option[User] = {
    if (id == null) { throw new IllegalArgumentException }
    ESClient.init()
    val user = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("_id", id))
      }.map(_._2)
    }
    ESClient.shutdown()
    user
  }

  /** emailを受け取ってUserを検索した結果をOptionに詰めて返す
    * @param email Userのemail (NotNull)
    * @return 検索結果
    */
  def findByEmail(email: String): Option[User] = {
    if (email == null) { throw new IllegalArgumentException }
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

  /** Sesssionからユーザーを一意に識別するカラム:Emailを取得する
    * @param request リクエスト
    * @return メールアドレス
    */
  def getIdentifier(request: Request[Any]): String = {
    if (request == null) { throw new IllegalArgumentException }
    Logger.debug(request.toString)
    request.session.get("auth").getOrElse("Guest")
  }
}
