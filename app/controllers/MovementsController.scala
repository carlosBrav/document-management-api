package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
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
