package helpers

import play.api.libs.functional.syntax._
import utils.RequestUtils._
import play.api.libs.json.{Format, Json, OFormat, OWrites, Reads, __}
import java.sql.Timestamp

object AuthControllerHelper {

  case class RequestLogin(usuario: String, password: String)
  val requestLoginReads: Reads[RequestLogin] = (
    emptyStringFieldCheck("usuario","El usuario es requerido") and
      emptyStringFieldCheck("password","La contraseña es requerida")
  )(RequestLogin.apply _)
  val requestLoginWrites: OWrites[RequestLogin] = Json.writes[RequestLogin]
  implicit val requestLogin: Format[RequestLogin] = Format(requestLoginReads, requestLoginWrites)

  case class ResponseLogin(id: Option[String],
                           usuario: String,
                           token: String,
                           estado: Boolean,
                           rolId: String,
                           nombre: String,
                           apellido: String,
                           telefono: Option[String],
                           fechaCreacion: Option[String],
                           fechaModificacion: Option[String]
                          )
  implicit val responseLogInFormat: OFormat[ResponseLogin] = Json.format[ResponseLogin]

  case class RequestUpdateUser(
                              id: Option[String],
                              usuario: String,
                              token: String,
                              estado: Boolean,
                              rolId: String,
                              nombre: String,
                              apellido: String,
                              telefono: Option[String]
                              )
  implicit val requestUpdateUser: OFormat[RequestUpdateUser] = Json.format[RequestUpdateUser]

}