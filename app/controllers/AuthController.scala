package controllers

import helpers.AuthControllerHelper._
import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._
import services.UserService
import utils.Constants._
import utils.Constants.Implicits._
import utils._
import utils.BCryptPass._

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
        userService.processLogin(userRequest.user, userRequest.password) map {
          case Success(userRolResult) =>
            val (users,rolRes) = userRolResult
            val token = JWTUtil.createToken(Map(
              "userId" -> users.id.get,
              "usuario" -> users.usuario,
              "roleId" -> users.rolId,
              "rol" -> rolRes.get.nombre,
              "nombre" -> users.nombre,
              "apellido" -> users.apellido))
            JsonOk(
              Response[ResponseLogin](ResponseCodes.SUCCESS,"success",
                ResponseLogin(users.id, users.usuario, token, users.estado, users.rolId, rolRes.get.nombre,
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

  def changePassword(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[RequestChangePassword].fold(
      invalidRequest => {
        val errors =  invalidResponseFormatter(invalidRequest)
        val found = Constants.get(ResponseCodes.MISSING_FIELDS)
        Future.successful(
          Ok(Json.toJson(ResponseErrorLogin[Seq[String]](ResponseCodes.MISSING_FIELDS, s"${found.message}", errors)))
        )
      },
      userRequest => {
        val result = for {
          Success(userDB) <- userService.loadById(id)
          _ <- if(validateHashPass(userRequest.oldPassword, userRequest.newPassword)){
           val newUser = userDB.copy(password = createHashPass(userRequest.newPassword))
            Future(userService.updateById(id,newUser))
          }else{
          Future.failed(PasswordNotMatchedException("Contraseña invalida"))
          }
        }yield JsonOk(
          Response[String](ResponseCodes.SUCCESS, "success", s"Se ha actualizado correctamente la contraseña")
        )
        result recover {
          case e: PasswordNotMatchedException =>
            JsonOk(
              ResponseError[String](ResponseCodes.PASSWORD_NOT_MATCH, e.getMessage)
            )
          case _ => JsonOk(
            ResponseError[String](ResponseCodes.GENERIC_ERROR, s"Error al intentar actualizar la contraseña")
          )
        }
      }
    )
  }
}
