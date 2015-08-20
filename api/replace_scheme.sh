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
  "interests": ["Java", "Scala", "Node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'
             
# konoaraiwotsukuttanohadareda        
es POST user/bishokuKurabuNoAruji '
{
  "accountName": "海原雄山",
  "email": "kaibara@bishoku.club",
  "interests": ["HTML", "CSS", "JavaScript", "Scalaz"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

# watashihashicho
es POST user/metroCityNoSichodayo '
{
  "accountName": "マイク・ハガー",
  "email": "mike@metro-city.go.jp",
  "interests": ["Lisp", "Scala"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

# musekinin
es POST user/spacedeka '
{
  "accountName": "宇宙刑事ギャバン",
  "email": "gaban@spacedeka.com",
  "interests": ["Scala", "Java"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/koukakuKidoutaiMitai '
{
  "accountName": "草薙素子",
  "email": "kusanagi@kouan-9.go.jp",
  "interests": ["Haskell", "Clojure", "Haxe", "Scala"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/kusamayayoi '
{
  "accountName": "草間彌生",
  "email": "kusama@yayoi.com",
  "interests": ["Vim", "Linux", "Scala", "Go"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/luckeyIkeda '
{
  "accountName": "ラッキー池田",
  "email": "luckey@ikeda.com",
  "interests": ["Python", "Java", "Node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/okawaEisaku '
{
  "accountName": "大川栄策",
  "email": "okawa@eisaku.com",
  "interests": ["Python", "Ruby", "Node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/pomupomupurin '
{
  "accountName": "ポムポムプリン",
  "email": "pomupomu@purin.com",
  "interests": ["Ruby", "ShellScript", "Node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/kanahei '
{
  "accountName": "カナヘイ",
  "email": "kanahei@hey.com",
  "interests": ["Ruby", "ShellScript", "Node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/dragonkid '
{
  "accountName": "ドラゴン・キッド",
  "email": "kid@dragon-gate.com",
  "interests": ["Emacs", "Scala", "Ruby"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

es POST user/chocoboy '
{
  "accountName": "チョコボーイ山口",
  "email": "yamaguchi@choco-boy.com",
  "interests": ["ShellScript", "Linux", "Go"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
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
  "userId": "bishokuKurabuNoAruji",
  "code": "User.findByArray(this))",
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
  "userId": "metroCityNoSichodayo",
  "code": "kill 9999",
  "description": "9999番のプロセスを殺す",
  "tag": "ShellScript",
  "time": "2015/03/12 12:46"
}'

es POST post '
{
  "userId": "metroCityNoSichodayo",
  "code": "var x = \"123\";var y = +x;",
  "description": "文字列を数値化",
  "tag": "JavaScript",
  "time": "2015/08/12 2:46"
}'

es POST post '
{
  "userId": "koukakuKidoutaiMitai",
  "code": "brew install ghosthack",
  "description": "ゴーストハックが使えるようになる",
  "tag": "brew",
  "time": "2015/08/10 12:46"
}'

es POST post '
{
  "userId": "metroCityNoSichodayo",
  "code": "sudo brew install -g ghosthack",
  "description": "グローバルに使えたほうがいい",
  "tag": "brew",
  "time": "2015/08/12 12:46"
}'

es POST post '
{
  "userId": "luckeyIkeda",
  "code": "Math.sin()",
  "description": "いい感じにモタったりハシったりすることでグルーヴが生まれるんだ！",
  "tag": "Dance",
  "time": "2015/08/18 12:46"
}'

es POST post '
{
  "userId": "luckeyIkeda",
  "code": "Math.cos()",
  "description": "こちらも同様",
  "tag": "Dance",
  "time": "2015/08/12 12:46"
}'

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
