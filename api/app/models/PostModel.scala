package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import org.joda.time.DateTime

/**
 * @author shunsuke tadokoro
 */

case class Post(userId: String, code: String, description: String, tag: String, time: String)
case class ShownPost(id: String, post: Post, user: User, isFavorite: Boolean)

object Post {

  val config = "code_snip" / "post"
  val url = "http://localhost:9200"

  def selectPostById(id: String): Option[(String, Post)] = {
    ESClient.using(url) { client =>
      client.find[Post](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
  }

  def selectPostListByUserId(id: String): List[ShownPost] = {
   ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", id)).addSort("_timestamp", SortOrder.DESC)
      }
    }.list.map(
     x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2, Favorite.isFavorite(id, x.doc)))
  }

  def selectFollowPost(id: String): List[ShownPost] = {
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", Follow.selectFollowListByUserId(id)))
      }
    }.list.map(x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2, Favorite.isFavorite(id, x.doc)))
    postList
  }

  def getCurrentDateTime:String = {
    DateTime.now.toString("yyyy/MM/dd HH:mm")
  }

}
