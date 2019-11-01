package helpers

import helpers.UsersControllerHelper.userModelResponse
import models.Usuario
import play.api.libs.json.{JsObject, Json, OFormat}

object HomeControllerHelper {

  case class ResponseDependency(id: Option[String],
                                 nombre: String,
                                 estado: Boolean,
                                 siglas: Option[String],
                                 codigo: String,
                                 tipo: String)
  implicit val responseDependencyFormat: OFormat[ResponseDependency] = Json.format[ResponseDependency]

  case class ResponseUser(id: Option[String],
                               usuario: String,
                               estado: Boolean,
                               rolId: String,
                               nombre: String,
                               apellido: String,
                               telefono: Option[String],
                               dependenciaId: Option[String],
                               isSubOfficeBoss: Boolean,
                               isOfficeBoss: Boolean)

  implicit val responseUserFormat: OFormat[ResponseUser] = Json.format[ResponseUser]

  case class InitialStateResponse(responseCode: Int, responseMessage: String, data: JsObject)
  implicit val initialStateResponseFormat: OFormat[InitialStateResponse] = Json.format[InitialStateResponse]

}
