package models

import jp.co.bizreach.elasticsearch4s._
import play.Logger
import play.api.libs.json.{JsResult, JsError}

/**
 * @author Shunsuke Tadokoro
 */
case class Follow(followFromId: String, followToId: String)
case class DisplayFollow(id: String, followFromId: String, followToId: String)

object Follow {

  val config = "code_snip" / "follow"
  val url = "http://localhost:9200"

  def addFollow(userId: String, targetId: String): Either[Map[String, Any], Map[String, Any]] = {
    ESClient.init()
    val result = ESClient.using(url) { client =>
      client.insert(config, Follow(followFromId = userId, followToId = targetId))
    }
    ESClient.shutdown()
    result
  }

  def removeFollow(userId: String, targetId: String): Either[Map[String, Any], Map[String, Any]] = {
    val result = ESClient.using(url) { client =>
      client.delete(config, detectFollowId(userId, targetId).getOrElse("-1")) // TODO
    }
    result
  }

  def selectFollowListByUserId(userId: String): List[String] = {
    ESClient.init()
    val followList = ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId))
      }
    }
    ESClient.shutdown()
    Logger.error(followList.list.toString)
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

  def detectFollowId(userId: String, targetId: String): Option[String] = {
    ESClient.init()
    val result = ESClient.using(url) { client =>
      client.find[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId)).setQuery(matchQuery("followToId", targetId))
      }
    }
    ESClient.shutdown()
    result.map(x => x._1)
  }


}

