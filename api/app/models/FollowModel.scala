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

  /** フォローの追加
    * @param userId フォローする側のユーザーID
    * @param targetId フォローされる側のユーザーID
    * @return 追加の成否
    */
  def addFollow(userId: String, targetId: String): Either[Map[String, _], Map[String, _]] = {
    ESClient.using(url) { client =>
      client.insert(config, Follow(followFromId = userId, followToId = targetId))
    }
  }

  /** フォローの解除
    * @param userId フォローしている側のユーザーID
    * @param targetId リムーブされる側のユーザーID
    * @return 解除の成否
    */
  def removeFollow(userId: String, targetId: String): Either[Map[String, _], Map[String, _]] = {
    ESClient.using(url) { client =>
      client.delete(config, detectFollowId(userId, targetId).getOrElse("-1"))
    }
  }

  /** 自分がフォローしているユーザーのリストを検索
    * @param userId フォローしている側のユーザーID
    * @return 検索結果
    */
  def selectFollowListByUserId(userId: String): List[String] = {
    ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId))
      }
    }.list.map(result => result.doc).map(e => e.followToId)
  }

  /** 自分をフォローしているユーザーのリストを検索
    * @param userId フォローされている側のユーザーID
    * @return 検索結果
    */
  def selectFollowerListByUserId(userId: String): List[String] = {
    ESClient.using(url) { client =>
      client.list[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followToId", userId))
      }
    }.list.map(result => result.doc).map(e => e.followFromId)
  }

  /** フォローとフォロワーからユーザーを特定
    * @param userId フォローしている側のユーザーID
    * @param targetId フォローされている側のユーザーID
    * @return 検索結果
    */
  private def detectFollowId(userId: String, targetId: String): Option[String] = {
    ESClient.using(url) { client =>
      client.find[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("followFromId", userId)).setQuery(matchQuery("followToId", targetId))
      }
    }.map(x => x._1)
  }
}

