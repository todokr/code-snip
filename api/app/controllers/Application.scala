package controllers

import play.api.mvc._

object Application extends Controller {

  def options(path:String) = Action { Ok("")}

  def preFlight(all: String) = Action {
    Ok.withHeaders("Access-Control-Allow-Origin" -> "*",
      "Allow" -> "*",
      "Access-Control-Allow-Methods" -> "POST, GET, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Origin, X-Requested-With, Content-Type, Accept, Referrer, User-Agent")
  }

}