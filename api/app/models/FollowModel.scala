package models

import jp.co.bizreach.elasticsearch4s._

/**
 * @author Shunsuke Tadokoro
 */
case class Follow(followFromId: String, followToId: String)
case class DisplayFollow(id: String, followFromId: String, followToId: String)

object Follow {

  val config = "code_snip" / "follow"
  val url = "http://localhost:9200"

  def addFollow(userId: String, targetId: String): Either[Map[String, Any], Map[String, Any]] = {
    val result = ESClient.using(url) { client =>
      client.insert(config, Follow(followFromId = userId, followToId = targetId))
    }
    result
  }

  def removeFollow(userId: String, targetId: String): Either[Map[String, Any], Map[String, Any]] = {
    val result = ESClient.using(url) { client =>
      client.delete(config, detectFollowId(userId, targetId).getOrElse("-1")) // TODO
    }
    result
  }

  def selectFollowListByUserId(userId: String): List[String] = {
    val followList = ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId))
      }
    }
    followList.list.map(result => result.doc).map(e => e.followToId)
  }

  def selectFollowerListByUserId(userId: String): List[String] = {
    val followList = ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followToId", userId))
      }
    }
    followList.list.map(result => result.doc).map(e => e.followFromId)
  }

  private def detectFollowId(userId: String, targetId: String): Option[String] = {
    val result = ESClient.using(url) { client =>
      client.find[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId)).setQuery(matchQuery("followToId", targetId))
      }
    }
    result.map(x => x._1)
  }


}
