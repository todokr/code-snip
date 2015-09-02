package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import play.api.libs.Crypto._
import play.api.mvc.Request

/**
 * @author shunsuke tadokoro
 */

case class User(accountName: String, email: String, interests: Seq[String], password: String, imageUrl: String)
case class DisplayUser(id:String, user:User, isFollowing: Boolean)

object User {

  val config = "code_snip" / "user"
  val url = "http://localhost:9200"
  
  def setCrypted(accountName: String, email: String, interests: Seq[String], password: String, imageUrl: String): User = {
    val cryptedPassword = sign(password)
    User(accountName, email, interests, cryptedPassword, imageUrl)
  }

  /** 新規ユーザーを保存する
    * @param user 新しく作成したユーザー
    */
  def insertUser(user: User): Unit = {
    val defaultImgUrl = "/assets/images/default.gif"
    ESClient.using(url) { client =>
      client.insert(config, User.setCrypted(accountName = user.accountName, email = user.email, interests = user.interests, password = user.password, defaultImgUrl))
    }
  }

  /** ユーザー情報を更新する
    * @param userTuple 更新対象のユーザーIDとユーザーのタプル
    * @param newUser 新しい情報になったユーザー
    * @return (id, User)
    */
  def updateUser(userTuple:(String, User), newUser: User): Either[Map[String,_], Map[String,_]] = {
    ESClient.using(url) { client =>
      val pass = if(newUser.password.isEmpty) userTuple._2.password else sign(newUser.password)
      val img = if(newUser.imageUrl.isEmpty) userTuple._2.imageUrl else newUser.imageUrl
      client.update(config, userTuple._1, User(accountName = newUser.accountName, email = newUser.email, interests = newUser.interests, password = pass, imageUrl = img))
    }
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
    * @param userId
    * @return Userの検索結果
    */
  def selectUserListFromInterests(userId: String): List[DisplayUser] = {
    val interestList = selectUserById(userId).map(u => u._2.interests)
    val followList = Follow.selectFollowListByUserId(userId)
    ESClient.using(url) { client =>
      client.list[User](config){ searcher =>
        searcher.setQuery(matchQuery("interests", interestList)).addSort("_score", SortOrder.DESC)
      }
    }.list.map(result => {
      val isFollowing = followList.contains(result.id)
      DisplayUser(result.id, result.doc, isFollowing)
    })
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

  def selectInterestListByUserId(userId: String): Option[Seq[String]] = {
    selectUserById(userId).map(u => u._2.interests)
  }
}

