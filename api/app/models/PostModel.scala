package models

import jp.co.bizreach.elasticsearch4s._

/**
 * @author shunsuke tadokoro
 */

case class Post(userId: String, code: String, description: String, tag: Seq[String])

object Post {

  val config = "code_snip" / "post"
  val url = "http://localhost:9200"

  def selectPostById(id:String): Option[(String, Post)] = {
    if (id == null) { throw new IllegalArgumentException }
    ESClient.init()
    val postData= ESClient.using(url) { client =>
      client.find[Post](config){ searcher =>
        searcher.setQuery(termQuery("_id", id))
      }
    }
    ESClient.shutdown()
    postData
  }
}
