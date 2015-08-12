package models

import jp.co.bizreach.elasticsearch4s._

/**
 * @author shunsuke tadokoro
 */

case class Post(userId: String, code: String, description: String, tag: Seq[String])

object Post {

  val config = "code_snip" / "post"
  val url = "http://localhost:9200"

  def selectPostById(id: String): Option[(String, Post)] = {
    ESClient.init()
    val postData= ESClient.using(url) { client =>
      client.find[Post](config){ searcher =>
        searcher.setQuery(matchQuery("_id", id))
      }
    }
    ESClient.shutdown()
    postData
  }

  def  selectPostListByUserId(id: String): List[ESSearchResultItem[Post]] = {
    if (id == null) { throw new IllegalArgumentException }
    ESClient.init()
    val postList = ESClient.using(url) { client =>
      client.list[Post](config){ searcher =>
        searcher.setQuery(matchQuery("userId", id))
      }
    }
    ESClient.shutdown()
    postList.list
  }
}
