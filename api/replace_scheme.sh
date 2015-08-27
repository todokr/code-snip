#!/bin/bash

node="code_snip"
domain="localhost"
port="9200"

es() {
  method=$1
  path=$2
  json=$3
  curl -X$method http://$domain:$port/$node/$path -d "$json"
  echo ""
}

# Message
echo ""
echo "//====================================//"
echo "//   SAW Elasticsearch ReplaceScheme  //"
echo "//====================================//"
echo ""
echo "connect to http://$domain:$port/$node"
echo ""

# ===================================================================================
#                                                                               Reset
#                                                                               =====
echo "<==== Reset ====>"
echo ""

# Reset
curl -XDELETE http://$domain:$port/*
echo ""
curl -XPOST http://$domain:$port/$node
echo ""

# ===================================================================================
#                                                                             Mapping
#                                                                             =======
echo ""
echo "<==== Mapping ====>"
echo ""

es PUT _mapping/user '
{
  "user": {
    "_index" : {"enabled": true},
    "_timestamp": {
      "enabled": true,
      "format": "YYYY-MM-dd HH:mm:ss.SSS",
      "store": true
    },
    "properties": {
      "accountName": {
        "index": "not_analyzed",
        "type": "string"
      },
      "email": {
        "index": "not_analyzed",
        "type": "string"
      },
      "interests" : {"type" : "string"},
      "password": {"type": "string"}
    }
  }
}'

es PUT _mapping/follow '
{
  "follow": {
    "_timestamp": {
      "enabled": true,
      "format": "YYYY-MM-dd HH:mm:ss.SSS",
      "store": "yes"
    },
    "properties": {
      "followFromId": {"type": "string"},
      "followToId": {"type": "string"}
    }
  }
}'

es PUT _mapping/post '
{
  "post": {
    "_id": {"store": true},
    "_timestamp": {
      "enabled": true,
      "format": "YYYY-MM-dd HH:mm:ss.SSS",
      "store": "yes"
    },
    "properties": {
      "userId": {"type": "string"},
      "code": {"type": "string"},
      "description": {"type": "string"},
      "forkFromId": {"type": "string"},
      "tag": {"type": "string"},
      "date": {"type": "string"}
    }
  }
}'

es PUT _mapping/promoted '
{
  "promoted": {
    "_id": {"store": true},
    "_timestamp": {
      "enabled": true,
      "format": "YYYY-MM-dd HH:mm:ss.SSS",
      "store": "yes"
    },
    "properties": {
      "userId": {"type": "string"},
      "code": {"type": "string"},
      "description": {"type": "string"},
      "linkUrl": {"type": "string"},
      "tag": {"type": "string"},
      "date": {"type": "string"}
    }
  }
}'

es PUT _mapping/favorite '
{
  "favorite": {
    "_timestamp": {
      "enabled": true,
      "format": "YYYY-MM-dd HH:mm:ss.SSS",
      "store": "yes"
    },
    "properties": {
      "userId": {"type": "string"},
      "postId": {"type": "string"}
    }
  }
}'

# ===================================================================================
#                                                                         Insert Data
#                                                                         ===========
echo ""
echo "<==== Insert ====>"
echo ""

# washigaotokojukujukucho
es POST user/o10kojukuedajima4649 '
{
  "accountName": "江田島平八",
  "email": "edajima@otoko-juku.ed.jp",
  "interests": ["Java", "Scala", "Perl"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'
             
# konoaraiwotsukuttanohadareda        
es POST user/bishokuKurabuNoAruji '
{
  "accountName": "海原雄山",
  "email": "kaibara@bishoku.club",
  "interests": ["HTML", "CSS", "JavaScript"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

# watashihashicho
es POST user/metroCityNoSichodayo '
{
  "accountName": "マイク・ハガー",
  "email": "mike@metro-city.go.jp",
  "interests": ["Lisp", "Scala"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

# musekinin
es POST user/spacedeka '
{
  "accountName": "宇宙刑事ギャバン",
  "email": "gaban@spacedeka.com",
  "interests": ["Scala", "Java"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/koukakuKidoutaiMitai '
{
  "accountName": "草薙素子",
  "email": "kusanagi@kouan-9.go.jp",
  "interests": ["Haskell", "Clojure", "Haxe", "Scala"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/kusamayayoi '
{
  "accountName": "草間彌生",
  "email": "kusama@yayoi.com",
  "interests": ["Vim", "Linux", "Scala", "Go"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/luckeyIkeda '
{
  "accountName": "ラッキー池田",
  "email": "luckey@ikeda.com",
  "interests": ["Python", "Java", "Perl"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/okawaEisaku '
{
  "accountName": "大川栄策",
  "email": "okawa@eisaku.com",
  "interests": ["Python", "Ruby", "Perl"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/pomupomupurin '
{
  "accountName": "ポムポムプリン",
  "email": "pomupomu@purin.com",
  "interests": ["Ruby", "ShellScript", "Perl"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "/assets/images/default.gif"
}'

es POST user/bizreach '
{
  "accountName": "株式会社ビズリーチ",
  "email": "test@bizreach.co.jp",
  "interests": ["Scala", "Java", "Linux"],
  "password": "276b3098174de28ef5877dacb412b825cf803192",
  "imageUrl": "data:image/png;base64,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"
}'



# Insert Follow Data
es POST follow '
{
  "followFromId": "koukakuKidoutaiMitai",
  "followToId": "spacedeka"
}'

es POST follow '
{
  "followFromId": "metroCityNoSichodayo",
  "followToId": "spacedeka"
}'

es POST follow '
{
  "followFromId": "o10kojukuedajima4649",
  "followToId": "koukakuKidoutaiMitai"
}'

es POST follow '
{
  "followFromId": "o10kojukuedajima4649",
  "followToId": "bishokuKurabuNoAruji"
}'

es POST follow '
{
  "followFromId": "koukakuKidoutaiMitai",
  "followToId": "o10kojukuedajima4649"
}'

es POST follow '
{
  "followFromId": "metroCityNoSichodayo",
  "followToId": "o10kojukuedajima4649"
}'

es POST follow '
{
  "followFromId": "metroCityNoSichodayo",
  "followToId": "kanahei"
}'

# Insert Withdrawal Data
es POST withdrawal '
{
  "userId": "4",
  "reason": "spacedeka"
}'

# Insert post Data
es POST post '
{
  "userId": "o10kojukuedajima4649",
  "code": "val otokojuku_jukucho = \"Washi\"",
  "description": "ワシが男塾塾長であることを定義",
  "tag": "Scala",
  "time": "2015/08/12 12:46"
}'

es POST post '
{
  "userId": "o10kojukuedajima4649",
  "code": "System.out.println \"江田島平八である！！！\"",
  "description": "江田島平八であることを出力",
  "tag": "Java",
  "time": "2015/08/13 12:46"
}'

es POST post '
{
  "userId": "o10kojukuedajima4649",
  "code": "戦え我等は仲間！ どんな組織でも一枚岩など・・・ ",
  "description": "エモい感じで",
  "tag": "ポエム",
  "time": "2015/08/23 12:46"
}'

es POST post '
{
  "userId": "bishokuKurabuNoAruji",
  "code": "User.findByArray(this)",
  "description": "この洗いを作ったのは誰かを探す",
  "tag": "Ruby",
  "time": "2015/08/12 1:46"
}'

es POST post '
{
  "userId": "bishokuKurabuNoAruji",
  "code": ".hoge {\n  width: 200px;\n  box-sizing: border-box;\n}",
  "description": "paddingやmargin込みのサイズでwidthを指定",
  "tag": "CSS",
  "time": "2015/08/12 1:43"
}'

es POST post '
{
  "userId": "bishokuKurabuNoAruji",
  "code": "window.addEventListener(\"WebComponentsReady\", function(){\n  // ここになにか処理\n});",
  "description": "カスタムエレメントの構築が完了した時点で何からの処理をする",
  "tag": "JavaScript",
  "time": "2015/08/12 1:41"
}'

es POST post '
{
  "userId": "metroCityNoSichodayo",
  "code": "kill 9999",
  "description": "9999番のプロセスを殺す",
  "tag": "Linux",
  "time": "2015/03/12 12:46"
}'

es POST post '
{
  "userId": "metroCityNoSichodayo",
  "code": "lsusb",
  "description": "USBポートに接続されているデバイスを表示",
  "tag": "Linux",
  "time": "2015/03/12 12:46"
}'

es POST post '
{
  "userId": "metroCityNoSichodayo",
  "code": "var x = \"123\"; \nvar y = +x;",
  "description": "文字列を数値化",
  "tag": "JavaScript",
  "time": "2015/08/12 2:46"
}'

es POST post '
{
  "userId": "spacedeka",
  "code": "String wakasa = \"振り向かないこと\";",
  "description": "若さって何だ",
  "tag": "Java",
  "time": "2015/08/23 2:12"
}'

es POST post '
{
  "userId": "spacedeka",
  "code": "String ai = \"ためらわないこと\";",
  "description": "愛って何だ",
  "tag": "Java",
  "time": "2015/08/23 2:14"
}'

es POST post '
{
  "userId": "spacedeka",
  "code": "{\n  \"message\": \"Validation Failed\",\n    \"errors\": [\n      {\n        \"resource\": \"Issue\",\n        \"field\": \"title\",\n      \"code\": \"missing_field\"\n    }\n    ]\n}",
  "description": "GitHubのエラー時のJSONレスポンス",
  "tag": "JavaScript",
  "time": "2015/08/24 2:14"
}'

es POST post '
{
  "userId": "koukakuKidoutaiMitai",
  "code": "yum install ghosthack",
  "description": "ゴーストハックをインストール",
  "tag": "Linux",
  "time": "2015/08/10 12:46"
}'

es POST post '
{
  "userId": "koukakuKidoutaiMitai",
  "code": "世の中に不満があるなら自分を変えろ！それが嫌なら、耳と目を閉じ、口をつぐんで孤独に暮らせ！！",
  "description": "世の中に不満があるなら",
  "tag": "ポエム",
  "time": "2015/08/20 12:46"
}'

es POST post '
{
  "userId": "koukakuKidoutaiMitai",
  "code": "Ghost.whisper(\"そうしろ\")",
  "description": "そうしろと囁くのよ、私のゴーストが",
  "tag": "Scala",
  "time": "2015/08/21 12:46"
}'

es POST post '
{
  "userId": "kusamayayoi",
  "code": "地球、月、太陽、そして人間も、全ては水玉で出来ているの。無数の水玉によってね。",
  "description": "全ては",
  "tag": "ポエム",
  "time": "2015/08/21 10:21"
}'

es POST post '
{
  "userId": "kusamayayoi",
  "code": "歳をとったからできなくなったことを嘆くよりも、\n前にはなかったエネルギーをたくさん作って、挑戦を続けたい。",
  "description": "求めれば求めるほど、星は遠く見える",
  "tag": "ポエム",
  "time": "2015/08/22 11:30"
}'

es POST post '
{
  "userId": "kusamayayoi",
  "code": "いま大切なのは、\n無や宇宙に対する人生観を立ち上げること、\n不老不死の人生観を立ち上げること。\nその中に、平和や愛を入れます。\n一番心がけることは、死を恐れないことです。",
  "description": "いま大切なのは",
  "tag": "ポエム",
  "time": "2015/08/23 12:12"
}'


es POST post '
{
  "userId": "luckeyIkeda",
  "code": "Math.sin()",
  "description": "いい感じにモタったりハシったりすることでグルーヴが生まれるんだ！",
  "tag": "JavaScript",
  "time": "2015/08/23 12:46"
}'

es POST post '
{
  "userId": "luckeyIkeda",
  "code": "var NS = window.NS || {};",
  "description": "名前空間を作る",
  "tag": "JavaScript",
  "time": "2015/08/24 15:23"
}'

es POST post '
{
  "userId": "luckeyIkeda",
  "code": "python -m SimpleHTTPServer 8080",
  "description": "簡易的なローカルサーバーをポート8080番で立ち上げる",
  "tag": "Dance",
  "time": "2015/08/25 5:16"
}'

es POST post '
{
  "userId": "okawaEisaku",
  "code": "$ echo \"hoge\"",
  "description": "hogeと出力する例",
  "tag": "Linux",
  "time": "2015/08/25 5:16"
}'

es POST post '
{
  "userId": "okawaEisaku",
  "code": "ruby -ne \"puts $_\" hoge.txt fuge.txt",
  "description": "hoge.txtとfuge.txtの内容を表示するワンライナー",
  "tag": "Ruby",
  "time": "2015/08/25 11:32"
}'

es POST post '
{
  "userId": "pomupomupurin",
  "code": "sleep 1000",
  "description": "1000msだけプログラムの実行を停止する",
  "tag": "Ruby",
  "time": "2015/08/22 19:26"
}'

es POST post '
{
  "userId": "pomupomupurin",
  "code": "STDIN.each {|line| puts line }",
  "description": "標準入力から読み込んで1行ずつ順繰りにputsする",
  "tag": "Ruby",
  "time": "2015/08/24 19:06"
}'

es POST post '
{
  "userId": "pomupomupurin",
  "code": "ruby -r open-uri -r json -e \"open(\"http://graph.facebook.com/zuck\") {|f| p JSON.parse(f.read)}\"",
  "description": "facebookのapiからマークザッカーバーグのJSONを取得しハッシュに変換",
  "tag": "Ruby",
  "time": "2015/08/24 11:22"
}'

es POST promoted '
{
  "userId": "bizreach",
  "code": "var hoge = if(!fuga.isEmpty).get \n// → Bizreachに転職だ。",
  "description": "こんなコードに嫌気が差したら",
  "tag": "Scala",
  "time": "2015/08/26 11:22",
  "linkUrl": "https://jp.stanby.com/ats/4c9ca58af2fa0b1b0edebee441631eb9d472ba2388dd0105c16c47c34c1f2b8b?order=0"
}'

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
