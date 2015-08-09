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
      "cryptedPassword": {"type": "string"}
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

es POST user '
{
  "accountName": "江田島平八",
  "email": "edajima@otoko-juku.ed.jp",
  "interests": "java, scala, node.js",
  "cryptedPassword": "washigaotokojukujukucho"
}'

es POST user '
{
  "accountName": "海原雄山",
  "email": "kaibara@bishoku.club",
  "interests": "html, css, javascript",
  "cryptedPassword": "konoaraiwotsukuttanohadareda"
}'

es POST user '
{
  "accountName": "マイク・ハガー",
  "email": "mike@metro-city.go.jp",
  "interests": "lisp",
  "cryptedPassword": "watashihashicho"
}'

es POST user '
{
  "accountName": "無責任艦長タイラー",
  "email": "tyler@musekinin.com",
  "interests": "",
  "cryptedPassword": "musekinin"
}'

es POST user '
{
  "accountName": "草薙素子",
  "email": "kusanagi@kouan-9.go.jp",
  "interests": ["haskell", "clojure", "haxe"],
  "cryptedPassword": "ghosthack"
}'

# Insert Follow Data
es POST follow '
{
  "followFromId": "1",
  "followToId": "2"
}'

es POST follow '
{
  "followFromId": "1",
  "followToId": "3"
}'

es POST follow '
{
  "followFromId": "2",
  "followToId": "3"
}'

es POST follow '
{
  "followFromId": "4",
  "followToId": "1"
}'

# Insert Withdrawal Data
es POST withdrawal '
{
  "userId": "4",
  "reason": "無責任だから"
}'

# Insert post Data
es POST post/1 '
{
  "userId": "1",
  "code": "val jukucho = \"Washi\"",
  "description": "ワシが男塾塾長",
  "tag": "Scala"
}'

es POST post/2 '
{
  "userId": "1",
  "code": "print \"江田島平八である！！！\"",
  "description": "江田島平八である！！！",
  "tag": "Java"
}'

es POST post/3 '
{
  "userId": "2",
  "code": "User.findBy(id).with(Array(this))",
  "description": "この洗いを作ったのは誰かを探す",
  "tag": "Ruby"
}'

es POST post/4 '
{
  "userId": "2",
  "code": "kill 9999",
  "description": "9999番のプロセスを殺す",
  "tag": "ShellScript"
}'

es POST post/5 '
{
  "userId": "4",
  "code": "var x = \"123\"\n;var y = +x;"
  "description": "文字列を数値化",
  "tag": "JavaScript"

}'

es POST post/6 '
{
  "userId": "5",
  "code": "brew install ghosthack"
  "description": "ゴーストハックが使えるようになる",
  "tag": "brew"
}'

es POST post/7 '
{
  "userId": "1",
  "forkFromId": "6",
  "code": "sudo brew install -g ghosthack"
  "description": "グローバルに使えたほうがいい",
  "tag": "brew"
}'

es POST favorite '
{
  "userId": "1",
  "postId": "3"
}'

es POST favorite '
{
  "userId": "2",
  "postId": "1"
}'

es POST favorite '
{
  "userId": "3",
  "postId": "1"
}'

es POST favorite '
{
  "userId": "4",
  "postId": "6"
}'

es POST favorite '
{
  "userId": "2",
  "postId": "6"
}'

es POST favorite '
{
  "userId": "3",
  "postId": "6"
}'

es POST favorite '
{
  "userId": "5",
  "postId": "7"
}'

# Message
echo ""
echo "//====================================//"
echo "//                Complete            //"
echo "//====================================//"
echo ""
