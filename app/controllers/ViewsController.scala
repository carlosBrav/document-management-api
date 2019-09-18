package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import services.ViewService
import utils.Constants._
import utils.Constants.Implicits._
import utils._
import helpers.ViewsControllerHelper._
import play.api.Logger


class ViewsController @Inject()(
                               viewService: ViewService,
                               cc: ControllerComponents
                               )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def loadView2: Action[AnyContent] = Action.async{ implicit request =>
    viewService
      .getAllView2("day")
      .map(view2=>
        JsonOk(ResponseListView2(ResponseCodes.SUCCESS, "success", view2.map(view => toResponseView2(view))))
      )
      .recover{
        case exception: Exception =>
          logger.error(s"exception ${exception.getMessage}")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al listar el view2"))
      }
  }
}
