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

  /** idを受け取ってUserを検索した結果をidとのタプルで返す
    * @param id Userのid (NotNull)
    * @return Optionに詰めた検索結果
  */
  def selectUserById(id: String): Option[(String, User)] = {
    if (id == null) { throw new IllegalArgumentException }
    ESClient.init()
    val userData = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("_id", id))
      }
    }
    ESClient.shutdown()
    userData
  }

  /** EmailからUserを検索した結果をidとのタプルで返す
    * @param email Userのid (NotNull)
    * @return Optionに詰めた検索結果
    */
  def selectUserByEmail[A](email: String): Option[(String, User)] = {
    if (email== null || email.isEmpty) { throw new IllegalArgumentException }
    ESClient.init()
    val userData = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(termQuery("email", email))
      }
    }
    ESClient.shutdown()
    userData
  }
  
  /** Sessionからログイン中ユーザーのメールアドレスを取得する
    * @param request リクエスト
    * @return メールアドレス
    */
  def selectEmailBySession(request: Request[Any]): String = {
    if (request == null) { throw new IllegalArgumentException }
    Logger.debug(request.toString)
    request.session.get("auth").getOrElse("Guest")
  }
  
  /** Sessionからログイン中ユーザーを取得する
    * @param request リクエスト
    * @return (id, User)
    */
  def selectUserBySession(request: Request[Any]): Option[(String, User)] = {
    if (request == null) { throw new IllegalArgumentException }
    val email =  selectEmailBySession(request)
    selectUserByEmail(email)
  }
  
}
