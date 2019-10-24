package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.{DocumentInternService, MovimientoService, UserService}

import scala.concurrent.{ExecutionContext, Future}
import utils.Constants._
import utils.Constants.Implicits._
import helpers.MovementsControllerHelper._
import helpers.UsersControllerHelper._
import play.api.libs.json.{JsValue, Json}
import utils._
import scala.util.Success


class UsersController @Inject()(
                                 userService: UserService,
                                 movimientoService: MovimientoService,
                                 documentService: DocumentInternService,
                                 cc: ControllerComponents
                               )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)


  def loadMovementsByOffice(officeId: String): Action[AnyContent] = Action.async { implicit request =>

    movimientoService.loadMovementsByOffice(officeId)
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

  def loadMovementsByTramNum(numTram: String): Action[AnyContent] = Action.async { implicit request =>

    movimientoService.loadMovementsByTramNum(numTram)
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

  def loadMovementsByCurrentDate: Action[AnyContent] = Action.async { implicit request =>

    movimientoService.loadMovementsByCurrentDate
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

  def getCorrelativeMax: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[maxCorrelativeRequest].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      correlativeRequest =>{
        val values = documentService.getMaxCorrelative(correlativeRequest.officeId,correlativeRequest.typeDocumentId)
        values.map(value => {
          JsonOk(
            Response[maxCorrelativeResponse](ResponseCodes.SUCCESS, "success",
              if(value.getOrElse(0) == 0){
                maxCorrelativeResponse("%05d".format(1),correlativeRequest.siglas, getCurrentYear().toString)
              }else{
                if(value.get.anio.get == getCurrentYear().toString){
                  maxCorrelativeResponse("%05d".format(value.get.numDocumento.get + 1),value.get.siglas.get, value.get.anio.get)
                }else{
                  maxCorrelativeResponse("%05d".format(1),value.get.siglas.get, value.get.anio.get)
                }
              }
            )
          )
        }).recover {
          case ex =>
            logger.error(s"error obteniendo max correlativo: $ex")
            JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error alobtener max correlativo"))
        }
      }
    )
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
          _ <- movimientoService.deleteMovement(movementRequest.movementsIds)
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

  def getOfficeBoss : Action[AnyContent] = Action.async { implicit request =>
    userService.getOfficeBoss
      .map(user =>{
        JsonOk(userResponse(ResponseCodes.SUCCESS, toUserModel(user.get)))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo usuario jefe: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener usuario jefe de oficina"))
      }
  }
}
