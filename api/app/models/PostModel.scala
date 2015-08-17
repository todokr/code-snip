package models

import jp.co.bizreach.elasticsearch4s._

/**
 * @author shunsuke tadokoro
 */

case class Post(userId: String, code: String, description: String, tag: String)
case class PostWithUser(post: Post, user:User)

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

  def selectPostListByUserId(id: String): List[PostWithUser] = {
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", id))
      }
    }.list.map(x => x.doc).map(u => PostWithUser(u, User.selectUserById(u.userId).get._2))
    postList
  }

  def selectFollowPost(id: String): List[PostWithUser] = {
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", Follow.selectFollowListByUserId(id)))
      }
    }.list.map(x => x.doc).map(u => PostWithUser(u, User.selectUserById(u.userId).get._2))
    postList
  }

}
