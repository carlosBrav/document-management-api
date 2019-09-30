package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.{DocumentInternService, MovimientoService, UserService}
import models.DocumentosInternos

import scala.concurrent.{ExecutionContext, Future}
import utils.Constants._
import utils.Constants.Implicits._
import helpers.MovementsControllerHelper._
import play.api.libs.json.{JsValue, Json}
import utils._
import scala.util.{Failure, Success}


class UsersController @Inject()(
                                 userService: UserService,
                                 movimientoService: MovimientoService,
                                 documentService: DocumentInternService,
                                 cc: ControllerComponents
                               )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def loadMovements(officeId: String): Action[AnyContent] = Action.async { implicit request =>

    movimientoService.loadMovementsToOffice(officeId)
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos de la oficina $officeId"))
      }
  }

  def updateDocumentConfirm: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestUpdateMovements].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      movementRequest => {
        val response = for {
          _ <- movimientoService.updateFechaIngMovements(movementRequest.userId,
            movementRequest.movementsIds, movementRequest.currentDate, movementRequest.asignadoA)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"${movementRequest.movementsIds.length} documentos grabados")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar actualizar las fechas de ingreso")
          )
        }
      }
    )
  }

  def deriveDocument(userId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestDeriveMovements].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      movementRequest => {
        val newMovements = movementRequest.toMovementsModel
        val response = for {
          _ <- movimientoService.saveMovements(newMovements)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"${movementRequest.movements.length} movimientos grabados")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def getCorrelativeMax(officeId: String, tipoDocuId: String): Action[AnyContent] = Action.async { implicit request =>
    documentService.getMaxCorrelative(officeId,tipoDocuId)
      .map(document =>
      JsonOk(
        Response[String](ResponseCodes.SUCCESS, "success", s"${document.numDocumento}")
      ))
      .recover {
        case ex =>
          logger.error(s"error obteniendo max correlativo: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error alobtener max correlativo"))
      }
  }

  def generateResponseToMovements(userId: String, officeId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestResponseToMovements].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      movementRequest => {
        val (newDocumentIntern, newMovement) = movementRequest.toMovementModel(userId,officeId)
        val response = for {
          _ <- documentService.generateResponseToMovement(newDocumentIntern, newMovement)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"documento creado ${newDocumentIntern.id} con movimiento ${newMovement.id}")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def deleteDocumentIntern(documentId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val result: Future[Result] = for {
      Success(document) <- documentService.loadById(documentId)
      _ <- Future.successful(documentService.updateById(documentId, document.copy(active = false)))
    } yield JsonOk(
      Response[String](ResponseCodes.SUCCESS, "Success", s"Documento ${document.id.get} eliminado")
    )
    result recover {
      case _ => JsonOk(
        ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al eliminar documento interno")
      )
    }
  }

  def deleteMovement(movementId: String) : Action[JsValue] = Action.async(parse.json) { implicit request =>
    val result = for {
      _ <- movimientoService.deleteMovement(movementId)
    } yield JsonOk(
      Response[String](ResponseCodes.SUCCESS, "Success", s"Movimiento eliminado con id: $movementId")
    )
    result recover {
      case _ => JsonOk(
        ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al eliminar el movimiento")
      )
    }
  }
}
