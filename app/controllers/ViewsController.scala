package controllers

import javax.inject.Inject
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import services.{MovimientoService, ViewService}
import utils.Constants._
import utils.Constants.Implicits._
import utils._
import helpers.ViewsControllerHelper._
import play.api.Logger


class ViewsController @Inject()(
                               viewService: ViewService,
                               movimiento: MovimientoService,
                               cc: ControllerComponents
                               )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def loadView2: Action[AnyContent] = Action.async{ implicit request =>

    val listResult = for {
      Success(view2) <-viewService.getAllView2
    } yield {
      JsonOk(ResponseListView2(ResponseCodes.SUCCESS, "success", view2.map(view => toResponseView2(view._1, view._2))))
    }
    listResult recover {
      case e =>
        val messageError = Constants.get(e.getMessage.toInt)
        JsonOk(
          ResponseError[String](e.getMessage.toInt, s"${messageError.message}")
        )
    }
  }

  def insertFromView2(userId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestInsertFromView2].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      movementRequest => {
        val movementsList = movementRequest.toMovimientosModels(userId)
        val response = movimiento.saveMovements(movementsList)
        response map {
          case Success(_) =>
            JsonOk(Response[String](ResponseCodes.SUCCESS, "success", s"${movementsList.length} documentos confirmados"))
          case Failure(ex) =>
            logger.error(s"error agregando movmimentos: $ex")
            JsonOk(Response[String](ResponseCodes.GENERIC_ERROR, "error", "No se pudieron agregar los documentos"))
        }
      }
    )

  }
}
