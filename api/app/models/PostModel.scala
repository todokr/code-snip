package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import org.joda.time.DateTime
import play.api.libs.json.{JsError, JsResult}

/**
 * @author shunsuke tadokoro
 */

case class FromViewPost(code: String, description: String, tag: String)
case class Post(userId: String, code: String, description: String, tag: String, time: String)
case class ShownPost(id: String, post: Post, user: User, isFavorite: Boolean)

object Post {

  val config = "code_snip" / "post"
  val url = "http://localhost:9200"

  /** 新規投稿を保存する
    * @param userId 投稿したユーザーのID
    * @param post 新しい投稿
    * @return 投稿の成否
    */
  def insertPost(userId: String, post: FromViewPost): Either[Map[String,_], Map[String,_]] = {
    ESClient.using(url) { client =>
      client.insert(config, Post(userId = userId, code = post.code, description = post.description, tag = post.tag, time = getCurrentDateTime))
    }
  }

  /** 投稿IDから投稿を検索する
    * @param id 投稿のID
    * @return 検索結果
    */
  def selectPostById(id: String): Option[(String, Post)] = {
    ESClient.using(url) { client =>
      client.find[Post](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
  }

  /** ユーザーIDから投稿を検索する
    * @param id ユーザーのID
    * @return 検索結果
    */
  def selectPostListByUserId(id: String): List[ShownPost] = {
   ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", id)).addSort("_timestamp", SortOrder.DESC)
      }
    }.list.map(
     x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2, Favorite.isFavorite(id, x.doc)))
  }

  /** フォローしているユーザーの投稿を検索する
    * @param id ユーザーのID
    * @return 検索結果
    */
  def selectFollowPost(id: String): List[ShownPost] = {
    ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", Follow.selectFollowListByUserId(id)))
      }
    }.list.map(x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2, Favorite.isFavorite(id, x.doc)))
  }

  /** 現在の日時を取得する
    * @return 現在日時(yyyy/MM/dd HH:mm)
    */
  def getCurrentDateTime:String = {
    DateTime.now.toString("yyyy/MM/dd HH:mm")
  }

}
