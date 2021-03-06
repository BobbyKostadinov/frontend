import akka.actor.ActorSystem
import app.{FrontendApplicationLoader, FrontendComponents}
import com.softwaremill.macwire._
import commercial.CommercialLifecycle
import commercial.controllers.{CommercialControllers, HealthCheck}
import commercial.model.capi.CapiAgent
import commercial.model.feeds.{FeedsFetcher, FeedsParser}
import commercial.model.merchandise.books.{BestsellersAgent, BookFinder, MagentoService}
import commercial.model.merchandise.events.{LiveEventAgent, MasterclassAgent}
import commercial.model.merchandise.jobs.{Industries, JobsAgent}
import commercial.model.merchandise.travel.TravelOffersAgent
import common.CloudWatchMetricsLifecycle
import common.Logback.LogstashLifecycle
import conf.switches.SwitchboardLifecycle
import conf.{CachedHealthCheckLifeCycle, CommonFilters}
import contentapi.{CapiHttpClient, ContentApiClient, HttpClient}
import controllers.Assets
import dev.{DevAssetsController, DevParametersHttpRequestHandler}
import http.CorsHttpErrorHandler
import model.ApplicationIdentity
import play.api.ApplicationLoader.Context
import play.api._
import play.api.http.{HttpErrorHandler, HttpRequestHandler}
import play.api.libs.ws.WSClient
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import router.Routes

class AppLoader extends FrontendApplicationLoader {
  override def buildComponents(context: Context): FrontendComponents = new BuiltInComponentsFromContext(context) with AppComponents
}

trait CommercialServices {
  def wsClient: WSClient
  def actorSystem: ActorSystem

  lazy val magentoService = wire[MagentoService]
  lazy val capiHttpClient: HttpClient = wire[CapiHttpClient]
  lazy val contentApiClient = wire[ContentApiClient]

  lazy val bookFinder = wire[BookFinder]
  lazy val bestsellersAgent = wire[BestsellersAgent]
  lazy val liveEventAgent = wire[LiveEventAgent]
  lazy val masterclassAgent = wire[MasterclassAgent]
  lazy val travelOffersAgent = wire[TravelOffersAgent]
  lazy val jobsAgent = wire[JobsAgent]
  lazy val capiAgent = wire[CapiAgent]
  lazy val industries = wire[Industries]

  lazy val feedsFetcher = wire[FeedsFetcher]
  lazy val feedsParser = wire[FeedsParser]
}

trait AppComponents extends FrontendComponents with CommercialControllers with CommercialServices {

  lazy val devAssetsController = wire[DevAssetsController]
  lazy val healthCheck = wire[HealthCheck]
  lazy val assets = wire[Assets]

  override lazy val lifecycleComponents = List(
    wire[LogstashLifecycle],
    wire[CommercialLifecycle],
    wire[SwitchboardLifecycle],
    wire[CloudWatchMetricsLifecycle],
    wire[CachedHealthCheckLifeCycle]
  )

  lazy val router: Router = wire[Routes]

  lazy val appIdentity = ApplicationIdentity("frontend-commercial")

  override lazy val httpErrorHandler: HttpErrorHandler = wire[CorsHttpErrorHandler]
  override lazy val httpFilters: Seq[EssentialFilter] = wire[CommonFilters].filters
  override lazy val httpRequestHandler: HttpRequestHandler = wire[DevParametersHttpRequestHandler]
}
