package models

import jp.co.bizreach.elasticsearch4s._
import org.elasticsearch.search.sort.SortOrder
import play.Logger

/**
 * @author Shunsuke Tadokoro
 */
case class Favorite(userId: String, postId: String)
case class ShownFavorite(id: String, post: Post, user: User)

object Favorite extends SnipConfProvider {

  val config = snipConf.es.favoriteConfig.config
  val url = snipConf.es.favoriteConfig.url

  /** お気に入りを保存する
    * @param userId お気に入りを追加するユーザーのID
    * @param postId お気に入りに追加する投稿のID
    * @return 保存の成否
    */
  def insertFavorite(userId: String, postId: String): Either[Map[String,_], Map[String, _]] = {
    ESClient.using(url) { client =>
      client.insert(config, Favorite(userId, postId))
    }
  }

  /** お気に入りを削除する
    * @param userId お気に入りを削除するユーザーのID
    * @param postId お気に入りに削除する投稿のID
    * @return 保存の成否
    */
  def removeFavorite(userId: String, postId: String): Either[Map[String, _], Map[String, _]] = {
    ESClient.using(url) { client =>
      client.delete(config, detectFavoriteId(userId, postId).getOrElse("-1"))
    }
  }

  /** お気に入りリストを取得する
    * @param userId お気に入りに追加したユーザーのID
    * @return お気に入りのリスト
    */
  def selectFavoriteList(userId: String): List[ShownPost] = {
    val emptyUser = User("", "", Seq(), "", "")
    val emptyPost = Post("", "", "", "", "")
    ESClient.using(url) { client =>
      client.list[Favorite](config) { searcher =>
        searcher.setQuery(matchQuery("userId", userId)).addSort("_timestamp", SortOrder.DESC)
      }.list.map(e => {
        val postId = e.doc.postId
        val post = Post.selectPostById(postId).map(p => p._2).getOrElse(emptyPost)
        val user = User.selectUserById(post.userId).map(u => u._2).getOrElse(emptyUser)
        ShownPost(postId, post, user, true, userId == post.userId)
      })
    }
  }

  /** お気に入りかどうかの判定
    * @param userId ユーザーのID
    * @param post お気に入りかどうか判定される投稿
    * @return 判定結果
    */
  def isFavorite(userId: String, post: Post): Boolean = {
    selectFavoriteList(userId).map(fav => fav.post).contains(post)
  }

  /** プロモツイートがお気に入りかどうかの判定
    * @param userId ユーザーのID
    * @param post お気に入りかどうか判定されるプロモーテッドポスト
    * @return 判定結果
    */
  def isPromoFavorite(userId: String, post: PromotedPost): Boolean = {
    selectFavoriteList(userId).map(fav => fav.post).contains(post)
  }

  /** ユーザーIDと投稿からお気に入りIDを取得
    * @param userId ユーザーのID
    * @param postId 投稿のID
    * @return お気に入りID
    */
  private def detectFavoriteId(userId: String, postId: String): Option[String] = {
    ESClient.using(url) { client =>
      client.find[Follow](config) { searcher =>
        searcher.setQuery(matchQuery("userId", userId)).setQuery(matchQuery("postId", postId))
      }
    }.map(x => x._1)
  }
}
