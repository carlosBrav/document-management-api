package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{InternDocumentService, MovimientoService}
import helpers.MovementsControllerHelper._
import utils.Constants.{ResponseErrorLogin, invalidResponseFormatter}
import utils.{Constants, ResponseCodes}
import utils.Constants._
import utils.Constants.Implicits._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class MovementsController @Inject()(
                                     documentInternService: InternDocumentService,
                                     movementService: MovimientoService,
                                     cc: ControllerComponents
                                   )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

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
          _ <- movementService.updateFechaIngMovements(movementRequest.userId,
            movementRequest.movementsIds, movementRequest.currentDate, movementRequest.asignadoA)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"${movementRequest.movementsIds.length} documentos confirmados")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar actualizar las fechas de ingreso")
          )
        }
      }
    )
  }

  def deriveDocument: Action[JsValue] = Action.async(parse.json) { implicit request =>
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
          _ <- movementService.saveDerivedMovements(movementRequest.userId, movementRequest.movements.map(_.id.get), newMovements)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"${movementRequest.movements.length} movimientos derivados.")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def deriveAssignedDocument: Action[JsValue] = Action.async(parse.json) { implicit request =>
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
          _ <- movementService.saveDerivedAssignedMovements(movementRequest.userId, movementRequest.movements.map(_.id.get), newMovements)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"${movementRequest.movements.length} movimientos derivados.")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def deleteMovements : Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestDeleteMovements].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      movementRequest => {
        val response = for {
          _ <- movementService.deleteMovement(movementRequest.movementsIds)
        }yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"Se han eliminado ${movementRequest.movementsIds.length} movimientos")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar eliminar movimientos")
          )
        }
      }
    )
  }


  def loadMovementsToAnalyze(): Action[AnyContent] = Action.async { implicit request =>

    movementService.loadMovementsToAnalyze()
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos para analizar: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al listar movimientos del usuario"))
      }
  }

  def loadMovementsByOffice(officeId: String): Action[AnyContent] = Action.async { implicit request =>

    movementService.loadMovementsByOffice(officeId)
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos de la oficina $officeId"))
      }
  }

  def loadUserMovementsByOffice(officeId: String) : Action[AnyContent] = Action.async { implicit request =>

    movementService.loadUserMovementsByOfficeId(officeId)
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos de la oficina $officeId"))
      }
  }

  def loadAdminMovementsByOffice(officeId: String) : Action[AnyContent] = Action.async { implicit request =>

    movementService.loadAdminMovementsByOffice(officeId)
      .map(movements =>
        JsonOk(ResponseAdminMovement(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseAdminMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos de la oficina $officeId"))
      }
  }

  def loadMovementsByTramNum(numTram: String): Action[AnyContent] = Action.async { implicit request =>

    movementService.loadMovementsByTramNum(numTram)
      .map(movements =>
        JsonOk(
          ResponseMovements(ResponseCodes.SUCCESS, "Success",
            movements.map(move =>
              toResponseMovements(move._1,move._2,move._3))
          )
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos"))
      }
  }

  def loadMovementsByCurrentDate: Action[AnyContent] = Action.async { implicit request =>

    movementService.loadMovementsByCurrentDate
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al listar movimientos"))
      }
  }

  def loadMovementsByAssignedTo(userId: String): Action[AnyContent] = Action.async { implicit request =>

    movementService.loadMovementsByAssignedTo(userId)
      .map(movements =>
        JsonOk(ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
      )
      .recover {
        case ex =>
          logger.error(s"error listando movmimentos: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al listar movimientos del usuario"))
      }
  }

  def advancedSearch: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestAdvancedSearch].fold(
      invalidRequest => {
        val errors = invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      request => {
        val response =  for {
          movements <- movementService.loadAdvancedSearch(request.numTram,request.observation,request.officeId)
        } yield JsonOk(
          ResponseMovements(ResponseCodes.SUCCESS, "Success", movements.map(move => toResponseMovements(move._1,move._2,move._3)))
        )
        response recover {
          case ex =>
            logger.error(s"error obteniendo advanced search: ${ex.getMessage}")
            JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar obtener los movimientos de busqueda avanzada")
          )
        }
      }
    )
  }

}
