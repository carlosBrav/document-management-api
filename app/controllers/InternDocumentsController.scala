package controllers

import helpers.AuthControllerHelper.RequestLogin
import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.InternDocumentService
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

class InternDocumentsController @Inject()(
                                           documentInternService: InternDocumentService,
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
            toResponseDocumentsInterns(Some(""), Some(""), Some(value._1),value._2,value._3, value._4, value._5,value._6)))
        )
      ).recover {
      case e =>
        logger.error("error loading circular documents: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos internos"))
    }
  }

  def getDocumentsAdmin(officeId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService.getInternDocumentsAdmin(officeId)
      .map(documents =>
        JsonOk(
          ResponseInternDocumentAdmin(ResponseCodes.SUCCESS,
            documents.map(
              value => toResponseAdminDocumentIntern(value._1,value._2,value._3,value._4)
            ))
        )
      )recover {
      case e =>
        logger.error("error loading documents admin: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos internos"))
    }
  }

  def getDocumentsByOfficeId(typeDocumentId: String, officeId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService.getInternDocumentsByOfficeId(typeDocumentId,officeId)
      .map(documents =>
        JsonOk(
          ResponseDocumentsInternsByUserId(ResponseCodes.SUCCESS,
            documents.map(value =>
              toResponseDocumentsInterns(Some(""), Some(""), Some(value._1),value._2,value._3, value._4, value._5,value._6)))
        )
      ).recover {
      case e =>
        logger.error("error loading circular documents: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos internos"))
    }
  }

  def getDocumentsByTypeDocument(typeDocumentId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService.getInternDocumentsByTypeDocument(typeDocumentId)
      .map(documents =>
        JsonOk(
          ResponseDocumentsInternsByUserId(ResponseCodes.SUCCESS,
            documents.map(value =>
              toResponseDocumentsInterns(Some(""), Some(""), Some(value._1),value._2,value._3, value._4, value._5,value._6)))
        )
      ).recover {
      case e =>
        logger.error("error loading circular documents: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los documentos internos"))
    }
  }

  def getCircularDocumentsByUserId(userId: String): Action[AnyContent] = Action.async { implicit request =>
    documentInternService.getCircularDocuments(userId)
      .map(documents =>
        JsonOk(
          ResponseCircularDocumentsByUserId(ResponseCodes.SUCCESS,
            documents.map(value =>
              toResponseCircularDocument(Some(value._1),value._2,value._3,value._4)))
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
            toResponseDetailsMovements(document._1, document._2)))
        )
      }).recover {
      case e =>
        logger.error("error loading circular details: " + e.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los detalles del documento circular"))
    }
  }

  def editInternDocument(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestEditInternDocument].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      internDocumentRequest => {
        val result = for {
          Success(internDocument) <- documentInternService.loadById(id)
          newInternDocument = internDocument.get.copy(
            userId = Some(internDocumentRequest.userId.getOrElse(internDocument.get.userId.get)),
            asunto = Some(internDocumentRequest.asunto.getOrElse(internDocument.get.asunto.get)),
            origenId = internDocumentRequest.origenId.getOrElse(internDocument.get.origenId)
          )
          _ <- documentInternService.updateById(id,newInternDocument)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"Se ha creado el documento correctamente")
        )
        result recover {
          case e =>
            println("error al actualizar DI ", e.getMessage)
            JsonOk(
              ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar actualizar documento interno")
            )
        }
      }
    )
  }

  def deleteDocuments : Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestDeleteDocuments].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      documentRequest => {
        val response = for {
          Success(movement) <- movementService.loadByInternDocumentIds(documentRequest.documentsIds)
          _ <- if(movement.isEmpty){
            documentInternService.deleteDocuments(documentRequest.documentsIds,List(""))
          } else documentInternService.deleteDocuments(documentRequest.documentsIds,movement.map(x=>x.previousMovementId.get))
        }yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"Se ha(n) eliminado ${documentRequest.documentsIds.length} documentos")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar eliminar movimientos")
          )
        }
      }
    )
  }

  def deleteDocumentById(documentId: String): Action[AnyContent] = Action.async { implicit request =>
    val result: Future[Result] = for {
      _ <- Future.successful(documentInternService.deleteById(documentId))
    } yield JsonOk(
      ResponseError[String](ResponseCodes.SUCCESS, s"Documento eliminado correctamente")
    )
    result recover {
      case t: Throwable =>
        logger.error(s"Delete a document by id exception ${t.getMessage}")
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Documento no ha podido ser eliminado"))
    }
  }

  def createInternDocument: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestCreateInternDocument].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      internDocumentRequest => {
        val newInternDocument = internDocumentRequest.toInternDocument
        val response = for {
          _ <- documentInternService.save(newInternDocument)
        }yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"Se ha creado el documento correctamente")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar eliminar movimientos")
          )
        }
      }
    )
  }
}


