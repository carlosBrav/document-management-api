package controllers

import java.util.Date

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.DependencyService
import utils.Constants._
import utils.Constants.Implicits._

import scala.concurrent.{ExecutionContext, Future}
import helpers.OfficesControllerHelper._
import helpers.HomeControllerHelper.toResponseDependency
import play.api.libs.json.{JsValue, Json}
import utils.{Constants, ResponseCodes}

import scala.util.Success

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

  def getOfficeById(officeId: String): Action[AnyContent] = Action.async { implicit request =>
    dependencyService.getById(officeId)
      .map(office =>{
        JsonOk(OfficeResponseModel(ResponseCodes.SUCCESS,toOfficeModel(office.get)))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo dependencia por Id: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener datos de la dependencia"))
      }
  }

  def deleteOffice(officeId: String): Action[AnyContent] = Action.async { implicit request =>
    val result: Future[Result] = for {
      office <- dependencyService.getById(officeId)
      _ <- Future.successful(
        dependencyService.updateById(office.get.id.get,
          office.get.copy(
            estado = !office.get.estado,
            fechaModificacion = Some(new java.sql.Timestamp(new Date().getTime))
          )))
    } yield  JsonOk(
      Response[String](ResponseCodes.SUCCESS,"Succes", s"Success")
    )
    result recover {
      case _ => JsonOk(
        ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se puede deshabilitar oficina")
      )
    }
  }

  def updateOffice: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UpdateOfficeRequest].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      officeRequest => {
        val result = for {
          officeOld <- dependencyService.getById(officeRequest.office.id)
          newOffice = officeOld.get.copy(
            nombre = officeRequest.office.nombre,
            codigo = officeRequest.office.codigo,
            siglas = officeRequest.office.siglas,
            tipo = officeRequest.office.tipo
          )
          _ <- dependencyService.updateById(officeRequest.office.id,newOffice)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "Success", "Success")
        )
        result recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se puede actualizar la dependencia")
          )
        }
      }
    )
  }

  def createOffice: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateOfficeRequest].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      officeRequest => {
        val result = for {
          _ <- dependencyService.save(toNewOffice(officeRequest.office))
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "Success", "Success")
        )
        result recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se pudo crear la dependencia")
          )
        }
      }
    )
  }



}
