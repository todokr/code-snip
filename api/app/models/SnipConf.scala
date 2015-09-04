package models

import jp.co.bizreach.elasticsearch4s._

/**
 * @author Shunsuke Tadokoro
 */

// この設定ファイルを使用する際にミックスインするトレイト
trait SnipConfProvider {
  lazy implicit val snipConf = SnipConf.load
}

// 各設定をまとめるケースクラス
case class SnipConf(
                     es: EsConf
                 // ,hoge: HogeConf
                 // ,fuga: FugaConf
                   )

// elastic-scala-http-client用の設定を格納するケースクラス
case class EsConf(
                   favoriteConfig     : EsClientConf,
                   followsConfig      : EsClientConf,
                   postsConfig        : EsClientConf,
                   promotedPostsConfig: EsClientConf,
                   usersConfig        : EsClientConf
                 )

// elastic-scala-http-client用の設定を司るケースクラス
case class EsClientConf(config: ESConfig, url: String)


object SnipConf {

  // 設定の値を格納する
  def load: SnipConf = {
    SnipConf(
      EsConf(
        favoriteConfig      = EsClientConf("code_snip" / "favorite", "http://localhost:9200"),
        followsConfig       = EsClientConf("code_snip" / "follow", "http://localhost:9200"),
        postsConfig         = EsClientConf("code_snip" / "post", "http://localhost:9200"),
        promotedPostsConfig = EsClientConf("code_snip" / "promoted", "http://localhost:9200"),
        usersConfig         = EsClientConf("code_snip" / "user", "http://localhost:9200")
      )
    )
  }
}