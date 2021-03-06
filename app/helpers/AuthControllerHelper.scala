package helpers

import play.api.libs.functional.syntax._
import utils.RequestUtils._
import play.api.libs.json.{Format, Json, OFormat, OWrites, Reads, __}

object AuthControllerHelper {

  case class RequestLogin(user: String, password: String)
  val requestLoginReads: Reads[RequestLogin] = (
    emptyStringFieldCheck("user","El usuario es requerido") and
      emptyStringFieldCheck("password","La contraseña es requerida")
  )(RequestLogin.apply _)
  val requestLoginWrites: OWrites[RequestLogin] = Json.writes[RequestLogin]
  implicit val requestLogin: Format[RequestLogin] = Format(requestLoginReads, requestLoginWrites)

  case class ResponseLogin(id: Option[String],
                           usuario: String,
                           token: String,
                           estado: Boolean,
                           rolId: String,
                           rolName: String,
                           nombre: String,
                           apellido: String,
                           telefono: Option[String],
                           dependencyId: String,
                           dependencyName: String,
                           dependencySiglas: String,
                           fechaCreacion: Option[String],
                           fechaModificacion: Option[String]
                          )
  implicit val responseLogInFormat: OFormat[ResponseLogin] = Json.format[ResponseLogin]

  case class RequestUpdateUser(
                              usuario: String,
                              token: String,
                              estado: Boolean,
                              rolId: String,
                              nombre: String,
                              apellido: String,
                              telefono: Option[String]
                              )
  implicit val requestUpdateUser: OFormat[RequestUpdateUser] = Json.format[RequestUpdateUser]

  case class RequestChangePassword(
                                  oldPassword: String,
                                  newPassword: String
                                  )
  implicit val requestChangePasswordFormat: OFormat[RequestChangePassword] = Json.format[RequestChangePassword]

  case class PasswordNotMatchedException(message: String) extends Exception
}