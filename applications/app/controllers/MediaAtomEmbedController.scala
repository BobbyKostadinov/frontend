package controllers

import com.gu.contentapi.client.model.v1.ItemResponse
import com.gu.contentatom.thrift.{AtomData, Atom => AtomApi}
import common._
import model.Cached.{RevalidatableResult, WithoutRevalidationResult}
import model._
import play.api.mvc._

import scala.concurrent.Future
import contentapi.ContentApiClient
import model.content.MediaAtom
import play.twirl.api.Html

class MediaAtomEmbedController(contentApiClient: ContentApiClient) extends Controller with Logging with ExecutionContexts {

  def render(id: String) = Action.async { implicit request =>
    lookup(s"atom/media/$id") map {
      case Left(model) => renderMediaAtom(model)
      case Right(other) => renderOther(other)
    }
  }

  def make(apiAtom: Option[AtomApi]): Option[MediaAtom] = {
    apiAtom map (a => {
      val id = a.id
      val defaultHtml = a.defaultHtml
      val mediaAtom = a.data.asInstanceOf[AtomData.Media].media
      MediaAtom.mediaAtomMake(id, defaultHtml, mediaAtom)
    })
  }



  private def lookup(path: String)(implicit request: RequestHeader) = {
    val edition = Edition(request)

    log.info(s"Fetching media atom: $path for edition $edition")

    val response: Future[ItemResponse] = contentApiClient.getResponse(contentApiClient.item(path, edition)

    )

    val result = response map { response =>

      val modelOption = make(response.media)

      modelOption match {
        case Some(x) => Left(x)
        case _ => Right(NotFound)
      }
    }

    result recover convertApiExceptions
  }

  private def renderOther(result: Result)(implicit request: RequestHeader) = result.header.status match {
    case 404 => NoCache(NotFound)
    case 410 => Cached(60)(WithoutRevalidationResult(Gone(views.html.videoEmbedMissing())))
    case _ => result
  }

  private def renderMediaAtom(model: MediaAtom)(implicit request: RequestHeader): Result = {
    val body: Html = model match {
      case youtube if model.assets.head.platform == "Youtube" => Html(views.html.fragments.atoms.youtube(model).toString)
      case _ => Html(views.html.fragments.atoms.media(model).toString)
  }
    val page: MediaAtomEmbedPage = MediaAtomEmbedPage(model, body)

    Cached(600)(RevalidatableResult.Ok(model match {
      case youtube if model.assets.head.platform == "Youtube" => {
        views.html.fragments.atoms.mediaEmbed(page)
      }
      case _ => views.html.fragments.atoms.mediaEmbed(page)
        }
      )
    )
  }
}