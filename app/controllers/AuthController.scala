package controllers

import helpers.AuthControllerHelper._
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import utils.Constants._
import utils.Constants.Implicits._
import utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AuthController @Inject()(
                                userService: UserService,
                                cc: ControllerComponents
                              )extends AbstractController(cc){

  implicit val ec: ExecutionContext = defaultExecutionContext

  def login: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestLogin].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        userService.processLogin(userRequest.usuario, userRequest.password) map {
          case Success(users) =>
            val token = JWTUtil.createToken(Map(
              "userId" -> users.id.get,
              "usuario" -> users.usuario,
              "role" -> users.rolId,
              "nombre" -> users.nombre,
              "apellido" -> users.apellido))
            JsonOk(
              Response[ResponseLogin](ResponseCodes.SUCCESS,"success",
                ResponseLogin(users.id, users.usuario, token, users.estado, users.rolId,
                  users.nombre, users.apellido, users.telefono, Option(convertToString(users.fechaCreacion)),
                  Option(convertToString(users.fechaModificacion))))
            )
          case Failure(e)=>
            val messageError = Constants.get(e.getMessage.toInt)
            JsonOk(
              ResponseError[String](e.getMessage.toInt, s"${messageError.message}")
            )
        }
      }
    )
  }

  def updateUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
  }
}
