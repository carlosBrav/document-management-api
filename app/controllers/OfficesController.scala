package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.DependencyService
import utils.Constants._
import utils.Constants.Implicits._

import scala.concurrent.ExecutionContext
import helpers.OfficesControllerHelper._
import helpers.HomeControllerHelper.toResponseDependency
import utils.ResponseCodes

class OfficesController @Inject()(
                                 dependencyService: DependencyService,
                                 cc: ControllerComponents
                                 )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def getAllOffices: Action[AnyContent] = Action.async { implicit request =>
    dependencyService.getAll
      .map(offices => {
        JsonOk(ListOfficesResponse(ResponseCodes.SUCCESS, offices.map(office => toResponseDependency(office))))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo lista de oficinas: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener lista de oficinas"))
      }
  }
}
