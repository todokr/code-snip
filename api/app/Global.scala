import filters.CORSFilter
import play.api.mvc.WithFilters
import play.api.{Application, GlobalSettings, Play}
import utils.ElasticsearchUtil

object Global extends WithFilters(CORSFilter()) with GlobalSettings {

  lazy val logger = play.api.Logger(this.getClass)

  override def onStart(app: Application): Unit =
    try {
      super.onStart(app)

      ElasticsearchUtil.init(Play.current)

    } catch {
      case ex: Throwable =>
        if (!Play.isDev(Play.current))
          logger.error("Starting up process failed !!!", ex)
        throw ex
    }
}
