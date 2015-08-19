package controllers

import play.api.mvc._

object Application extends Controller {

  def options(path:String) = Action { Ok("")}

}