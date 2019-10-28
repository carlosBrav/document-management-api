package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.DocumentInternService
import services.MovimientoService
import utils.Constants._
import utils.Constants.Implicits._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import helpers.DocumentInternControllerHelper._
import helpers.MovementsControllerHelper._
import org.apache.commons.lang3.exception.ExceptionUtils
import utils.{Constants, ResponseCodes}

import scala.util.{Failure, Success}

class DocumentInternController @Inject()(
                                        documentInternService: DocumentInternService,
                                        movementService: MovimientoService,
                                        cc: ControllerComponents
                                        )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def createDocumentCircular(officeId: String, userId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestCreateCircular].fold(
      invalidRequest => {
        val errors = invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        val (documentIntern, movements) = userRequest.toModels(userId, officeId)
        val response = documentInternService.createCirculars(documentIntern, movements)
        response map {
          case Success(_) =>
            JsonOk(
              Response[String](ResponseCodes.SUCCESS, "success", s"Se ha creado el documento circular, con ${movements.length} destinos")
            )
          case Failure(ex) =>
            logger.error("error creando documentos circulares: " + ex.getMessage)
            JsonOk(
              ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar crear documentos circulares")
            )
        }
      }
    )
  }

  def getDocumentsByUserId(userId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService.getInternDocuments(userId)
      .map(documents =>
        JsonOk(
          ResponseDocumentsInternsByUserId(ResponseCodes.SUCCESS,
            documents.map(value =>
            toResponseDocumentsInterns(Some(""), Some(""), Some(value._1),value._2,value._3, value._4)))
        )
      ).recover {
      case e =>
        logger.error("error loading circular documents: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos circulares"))
    }
  }

  def getCircularDetails(documentId: String): Action[AnyContent] = Action.async { implicit request =>
    movementService
      .getInternDocumentsByDocumentId(documentId)
      .map(documents => {
        JsonOk(
          ResponseCircularDetails(ResponseCodes.SUCCESS, documents.map(document =>
            toResponseModelMovements(document)))
        )
      }).recover {
      case e =>
        logger.error("error loading circular details: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los detalles del documento circular"))
    }
  }
}


