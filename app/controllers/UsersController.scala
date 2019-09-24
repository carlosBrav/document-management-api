package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.{MovimientoService, UserService}

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import utils.Constants._
import utils.Constants.Implicits._
import helpers.UsersControllerHelper._
import play.api.libs.json.{JsValue, Json}
import utils._


class UsersController @Inject()(
                               userService: UserService,
                               movimientoService: MovimientoService,
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

  def updateFechaIng: Action[JsValue] = Action.async(parse.json) { implicit request =>
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
          _ <- movimientoService.updateFechaIngMovements(movementRequest.userId, movementRequest.movementsIds)
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

}
