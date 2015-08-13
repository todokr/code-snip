package models

import jp.co.bizreach.elasticsearch4s._
import play.Logger

/**
 * @author Shunsuke Tadokoro
 */
case class Follow(followFromId: String, followToId: String)

object Follow {

  val config = "code_snip" / "follow"
  val url = "http://localhost:9200"

  def selectFollowListByUserId(userId: String): List[String] = {
    ESClient.init()
    val followList = ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId))
      }
    }
    ESClient.shutdown()
    followList.list.map(result => result.doc).map(e => e.followToId)
  }

  def selectFollowerListByUserId(userId: String): List[String] = {
    ESClient.init()
    val followList = ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followToId", userId))
      }
    }
    ESClient.shutdown()
    followList.list.map(result => result.doc).map(e => e.followFromId)
  }

  //def selectFollowerList: List[ESSearchResultItem[User]] = {}

  def follow: Unit = {

  }

  def unFollow: Unit = {

  }


}

