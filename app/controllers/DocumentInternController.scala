package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.DocumentInternService
import utils.Constants._
import utils.Constants.Implicits._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import helpers.DocumentInternControllerHelper._
import org.apache.commons.lang3.exception.ExceptionUtils
import utils.{Constants, ResponseCodes}

class DocumentInternController @Inject()(
                                        documentInternService: DocumentInternService,
                                        cc: ControllerComponents
                                        )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def loadAllDocumentsInterns(tipoDocuId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService
        .getDocumentsInternsByTipoDocuId(tipoDocuId)
      .map(documents =>
      JsonOk(ResponseAllDocumentsInterns(ResponseCodes.SUCCESS,"Success",documents.map(document => toResponseDocumentsInterns(Some(""),Some(""),Some(document))))))
    .recover {
      case e =>
        logger.error("error loading all documents interns: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos internos"))
    }
  }

  def createDocumentCircular(officeId: String, userId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestCreateCircular].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        val (documentIntern, movements) = userRequest.toModels(userId, officeId)
        val response = for{
          _ <- documentInternService.createCiculares(userId,officeId,documentIntern,movements)
        }yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"Se han creado ${movements.length} documentos circulares")
        )
        response recover {
          case e =>
            logger.error("error creando documentos circulares: " + e.getMessage)
            JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar crear documentos circulares")
          )
        }
      }
    )
  }
}
