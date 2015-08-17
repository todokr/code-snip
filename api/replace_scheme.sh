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
      "tag": {"type": "string"}
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
  "interests": ["java", "scala", "node.js"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'
             
# konoaraiwotsukuttanohadareda        
es POST user/bishokuKurabuNoAruji '
{
  "accountName": "海原雄山",
  "email": "kaibara@bishoku.club",
  "interests": ["html", "css", "javascript"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

# watashihashicho
es POST user/metroCityNoSichodayo '
{
  "accountName": "マイク・ハガー",
  "email": "mike@metro-city.go.jp",
  "interests": ["lisp"],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

# musekinin
es POST user/tottemoMusekinindayo '
{
  "accountName": "無責任艦長タイラー",
  "email": "tyler@musekinin.com",
  "interests": [],
  "password": "276b3098174de28ef5877dacb412b825cf803192"
}'

# ghosthack
es POST user/koukakuKidoutaiMitai '
{
  "accountName": "草薙素子",
  "email": "kusanagi@kouan-9.go.jp",
  "interests": ["haskell", "clojure", "haxe"],
  "password": "2fa6522d969d621bd7f5d91191eaece4f188c19a"
}'

# Insert Follow Data
es POST follow '
{
  "followFromId": "koukakuKidoutaiMitai",
  "followToId": "tottemoMusekinindayo"
}'

es POST follow '
{
  "followFromId": "metroCityNoSichodayo",
  "followToId": "tottemoMusekinindayo"
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

# Insert Withdrawal Data
es POST withdrawal '
{
  "userId": "4",
  "reason": "tottemoMusekinindayo"
}'

# Insert post Data
es POST post/001 '
{
  "userId": "o10kojukuedajima4649",
  "code": "val jukucho = \"Washi\"",
  "description": "ワシが男塾塾長",
  "tag": "Scala"
}'

es POST post/002 '
{
  "userId": "o10kojukuedajima4649",
  "code": "print \"江田島平八である！！！\"",
  "description": "江田島平八であると出力",
  "tag": "Java"
}'

es POST post/003 '
{
  "userId": "bishokuKurabuNoAruji",
  "code": "User.findBy(id).with(Array(this))",
  "description": "この洗いを作ったのは誰かを探す",
  "tag": "Ruby"
}'

es POST post/004 '
{
  "userId": "metroCityNoSichodayo",
  "code": "kill 9999",
  "description": "9999番のプロセスを殺す",
  "tag": "ShellScript"
}'

es POST post/005 '
{
  "userId": "metroCityNoSichodayo",
  "code": "var x = \"123\";var y = +x;",
  "description": "文字列を数値化",
  "tag": "JavaScript"

}'

es POST post/006 '
{
  "userId": "koukakuKidoutaiMitai",
  "code": "brew install ghosthack",
  "description": "ゴーストハックが使えるようになる",
  "tag": "brew"
}'

es POST post/007 '
{
  "userId": "metroCityNoSichodayo",
  "forkFromId": "006",
  "code": "sudo brew install -g ghosthack",
  "description": "グローバルに使えたほうがいい",
  "tag": "brew"
}'

es POST favorite '
{
  "userId": "koukakuKidoutaiMitai",
  "postId": "003"
}'

es POST favorite '
{
  "userId": "koukakuKidoutaiMitai",
  "postId": "001"
}'

es POST favorite '
{
  "userId": "metroCityNoSichodayo",
  "postId": "001"
}'

es POST favorite '
{
  "userId": "metroCityNoSichodayo",
  "postId": "002"
}'

es POST favorite '
{
  "userId": "o10kojukuedajima4649",
  "postId": "001"
}'

es POST favorite '
{
  "userId": "o10kojukuedajima4649",
  "postId": "002"
}'

es POST favorite '
{
  "userId": "o10kojukuedajima4649",
  "postId": "003"
}'

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
