package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import org.joda.time.DateTime
import play.Logger

/**
 * @author shunsuke tadokoro
 */

case class Post(userId: String, code: String, description: String, tag: String, time: String)
case class ShownPost(postId: String, post: Post, user:User)

object Post {

  val config = "code_snip" / "post"
  val url = "http://localhost:9200"

  def selectPostById(id: String): Option[(String, Post)] = {
    val postData= ESClient.using(url) { client =>
      client.find[Post](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
    postData
  }

  def selectPostListByUserId(id: String): List[ShownPost] = {
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", id)).addSort("_timestamp", SortOrder.DESC)
      }
    //}.list.map(x => x.doc).map(u => ShownPost(u, User.selectUserById(u.userId).get._2))
    }.list.map(x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2))
    Logger.debug(postList.toString)
    postList
  }

  def selectFollowPost(id: String): List[ShownPost] = {
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", Follow.selectFollowListByUserId(id)))
      }
    }.list.map(x => ShownPost(x.id ,x.doc, User.selectUserById(x.doc.userId).get._2))
    postList
  }

  def getCurrentDateTime:String = {
    val date = DateTime.now.toString("yyyy/MM/dd HH:mm")
    Logger.debug(date)
    date
  }

}
