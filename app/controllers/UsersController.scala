package controllers

import java.util.Date

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._
import services.{InternDocumentService, MovimientoService, UserService}

import scala.concurrent.{ExecutionContext, Future}
import utils.Constants._
import utils.Constants.Implicits._
import helpers.MovementsControllerHelper._
import helpers.UsersControllerHelper._
import play.api.libs.json.{JsValue, Json}
import utils._

import scala.util.{Failure, Success}


class UsersController @Inject()(
                                 userService: UserService,
                                 movimientoService: MovimientoService,
                                 documentService: InternDocumentService,
                                 cc: ControllerComponents
                               )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

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
          _ <- movimientoService.updateStatusToMovement(movementRequest.movement.id.get)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"documento creado ${newDocumentIntern.id.get} con movimiento ${newMovement.id.get}")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def generateResponseToMovementsAdmin(userId: String, officeId: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
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
          _ <- movimientoService.updateStatusToMovementAdmin(movementRequest.movement.id.get)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success",
            s"documento creado ${newDocumentIntern.id.get} con movimiento ${newMovement.id.get}")
        )
        response recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar derivar documentos")
          )
        }
      }
    )
  }

  def getOfficeBoss : Action[AnyContent] = Action.async { implicit request =>
    userService.getOfficeBoss
      .map(user =>{
        JsonOk(userSimpleResponse(ResponseCodes.SUCCESS, toUserSimpleModel(user.get)))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo usuario jefe: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener usuario jefe de oficina"))
      }
  }

  def getAllUsers: Action[AnyContent] = Action.async { implicit request =>
    userService.getAllUsers()
      .map(users =>{
        JsonOk(ListUserResponse(ResponseCodes.SUCCESS, users.map(user => toUserModel(user._1,user._2,user._3))))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo lista de usuarios: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener lista de usuarios"))
      }
  }

  def getUserById(userId: String): Action[AnyContent] = Action.async { implicit request =>
    userService.getById(userId)
      .map(user =>{
        JsonOk(userSimpleResponse(ResponseCodes.SUCCESS,toUserSimpleModel(user.get)))
      })
      .recover {
        case ex =>
          logger.error(s"error obteniendo usuario por Id: $ex")
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al obtener datos del usuario"))
      }
  }

  def deleteUser(userId: String): Action[AnyContent] = Action.async { implicit request =>
    val result: Future[Result] = for {
      Success(user) <- userService.loadById(userId)
      _ <- Future.successful(
        userService.updateById(user.id.get,
          user.copy(
            estado = !user.estado,
            fechaModificacion = Some(new java.sql.Timestamp(new Date().getTime))
          )))
    } yield  JsonOk(
      Response[String](ResponseCodes.SUCCESS,"Succes", s"Success")
    )
    result recover {
      case _ => JsonOk(
        ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se puede inactivar usuario")
      )
    }
  }

  def updateUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UpdateUser].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        val result = for {
          Success(userOld) <- userService.loadById(userRequest.user.id.get)
          newUser = userOld.copy(
            estado = userRequest.user.estado,
            nombre = userRequest.user.nombre,
            apellido = userRequest.user.apellido,
            telefono = userRequest.user.telefono,
            dependenciaId = userRequest.user.dependenciaId,
            rolId = userRequest.user.rolId,
            email = userRequest.user.email,
            fechaModificacion = Some(new java.sql.Timestamp(new Date().getTime))
          )
          _ <- userService.updateById(userRequest.user.id.get,newUser)
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "Success", "Success")
        )
        result recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se puede actualizar al Usuario")
          )
        }
      }
    )
  }

  def createUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[CreateUser].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        val result = for {
          _ <- userService.save(toNewUser(userRequest.user))
        } yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "Success", "Success")
        )
        result recover {
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, "No se pudo crear el usuario")
          )
        }
      }
    )
  }

}
