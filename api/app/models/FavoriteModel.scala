package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder

/**
 * @author Shunsuke Tadokoro
 */
case class Favorite(userId: String, postId: String)
case class ShownFavorite(id: String, post: Post, user: User)

object Favorite {

  val config = "code_snip" / "favorite"
  val url = "http://localhost:9200"

  def insertFavorite(userId: String, postId: String): Boolean = {
    ESClient.using(url) { client =>
      client.insert(config, Favorite(userId, postId))
    } match {
      case Left => false
      case _ => true
    }
  }

  def removeFavorite(userId: String, postId: String): Boolean = {
    ESClient.using(url) { client =>
      client.delete(config, detectFavoriteId(userId, postId).getOrElse("-1"))
    } match {
      case Left => false
      case _ => true
    }
  }

  def selectFavoriteList(userId: String): List[ShownFavorite] = {
    val emptyUser = User("", "", Seq(), "")
    val emptyPost = Post("", "", "", "", "")
    ESClient.using(url) { client =>
      client.list[Favorite](config) { searcher =>
        searcher.setQuery(matchQuery("userId", userId)).addSort("_timestamp", SortOrder.DESC)
      }.list.map(e =>
        ShownFavorite(
          e.id,
          Post.selectPostById(e.doc.postId).map(p => p._2).getOrElse(emptyPost),
          User.selectUserById(e.doc.userId).map(u => u._2).getOrElse(emptyUser)
        )
      )
    }
  }

  private def detectFavoriteId(userId: String, targetId: String): Option[String] = {
    val result = ESClient.using(url) { client =>
      client.find[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("userId", userId)).setQuery(matchQuery("postId", targetId))
      }
    }
    result.map(x => x._1)
  }
}
