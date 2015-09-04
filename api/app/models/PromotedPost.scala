package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
/**
 * @author Shunsuke Tadokoro
 */

case class PromotedPost(userId: String, code: String, description: String, tag: String, time: String, linkUrl: String)
case class ShownPromotedPost(id: String, post: PromotedPost, user: User, isFavorite: Boolean, isPromoted: Boolean)

object PromotedPost extends SnipConfProvider {

  val config = snipConf.es.promotedPostsConfig.config
  val url = snipConf.es.promotedPostsConfig.url

  /** プロモーテッドポストを1件取得する（嗜好に近い順）
    * @param userId ユーザーID
    * @return プロモーテッドポスト
    */
  def selectPromotedPost(userId: String): List[ShownPromotedPost] = {
    val interestList = User.selectInterestListByUserId(userId).getOrElse(List.empty)
    ESClient.using(url) { client =>
      client.list[PromotedPost](config){ searcher =>
        searcher.setQuery(matchQuery("tag", interestList)).addSort("_score", SortOrder.DESC)
      }
    }.list.map(r => {
      ShownPromotedPost(
        r.id,
        r.doc,
        User.selectUserById(r.doc.userId).map(u => u._2).getOrElse(User("","", Seq(), "", "")),
        Favorite.isPromoFavorite(r.id, r.doc),
        true
      )
    })
  }
}