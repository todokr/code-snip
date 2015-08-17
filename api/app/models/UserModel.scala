package models

import jp.co.bizreach.elasticsearch4s._
import play.Logger
import play.api.libs.Crypto._
import play.api.mvc.Request

/**
 * @author shunsuke tadokoro
 */

case class User(accountName: String, email: String, interests: Seq[String], password: String)

object User {

  val config = "code_snip" / "user"
  val url = "http://localhost:9200"
  
  def setCrypted(accountName: String, email: String, interests: Seq[String], password: String): User = {
    val cryptedPassword = sign(password)
    User(accountName, email, interests, cryptedPassword)
  }

  /** idを受け取ってUserを検索した結果を返す
    * @param id Userのid (NotNull)
    * @return Optionに詰めた検索結果
  */
  def selectUserById(id: String): Option[(String, User)] = {
    val userData = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
    userData
  }

  /** EmailからUserを検索した結果をidとのタプルで返す
    * @param email Userのid (NotNull)
    * @return Optionに詰めた検索結果
    */
  def selectUserByEmail[A](email: String): Option[(String, User)] = {
    val userData = ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(matchQuery("email", email))
      }
    }
    userData
  }

  /** UserIDのリストからUserオブジェクトを検索しリストにして返す
    * @param userIdList UserIDのリスト
    * @return Userの検索結果
    */
  def selectUserListFromIdList(userIdList: List[String]): ESSearchResult[User] = {
    val userList = ESClient.using(url) { client =>
      client.list[User](config){ searcher =>
        searcher.setQuery(matchQuery("_id", userIdList))
      }
    }
    userList
  }
  
  /** Sessionからログイン中ユーザーのメールアドレスを取得する
    * @param request リクエスト
    * @return メールアドレス
    */
  def selectEmailBySession(request: Request[Any]): String = {
    request.session.get("auth").getOrElse("Guest")
  }
  
  /** Sessionからログイン中ユーザーを取得する
    * @param request リクエスト
    * @return (id, User)
    */
  def selectUserBySession(request: Request[Any]): Option[(String, User)] = {
    val email =  selectEmailBySession(request)
    selectUserByEmail(email)
  }
}

