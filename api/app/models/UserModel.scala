package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import play.Logger
import play.api.libs.Crypto._
import play.api.mvc.Request

/**
 * @author shunsuke tadokoro
 */

case class User(accountName: String, email: String, interests: Seq[String], password: String)
case class IdWithUser(id:String, user:User)

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
    ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
  }

  /** EmailからUserを検索した結果をidとのタプルで返す
    * @param email Userのid (NotNull)
    * @return Optionに詰めた検索結果
    */
  def selectUserByEmail[A](email: String): Option[(String, User)] = {
    ESClient.using(url) { client =>
      client.find[User](config){ searcher =>
        searcher.setQuery(matchQuery("email", email))
      }
    }
  }

  /** 興味あるキーワードのリストを一つでも含むUserオブジェクトを検索しリストにして返す
    * @param interestList キーワードのリスト。
    * @return Userの検索結果
    */
  def selectUserListFromInterests(interestList: Seq[String]): List[IdWithUser] = {
    ESClient.using(url) { client =>
      client.list[User](config){ searcher =>
        searcher.setQuery(matchQuery("interests", interestList)).addSort("_score", SortOrder.DESC)
      }
    }.list.map(result => IdWithUser(result.id, result.doc))
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

