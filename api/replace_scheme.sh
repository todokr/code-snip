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
  "userId": "okawaEisaku",
  "code": "ruby -r open-uri -e \"open(\"http://www.yahoo.co.jp\") {|f| puts f.read}\",
  "description": "yahooのhtmlを取得するワンライナー",
  "tag": "Ruby",
  "time": "2015/08/25 13:12"
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

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
