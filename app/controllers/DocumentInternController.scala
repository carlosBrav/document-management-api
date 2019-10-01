package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.DocumentInternService
import utils.Constants._
import utils.Constants.Implicits._
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.ExecutionContext
import helpers.DocumentInternControllerHelper._
import org.apache.commons.lang3.exception.ExceptionUtils
import utils.ResponseCodes

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

  def createDocumentCircular(officeId: String, userId: String): Action[JsValue] = Action.async { implicit request =>

  }
}
