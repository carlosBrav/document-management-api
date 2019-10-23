package helpers

import play.api.libs.json.{JsObject, Json, OFormat}

object HomeControllerHelper {

  case class ResponseDependency(id: Option[String],
                                 nombre: String,
                                 estado: Boolean,
                                 siglas: Option[String],
                                 codigo: String,
                                 tipo: String)
  implicit val responseDependencyFormat: OFormat[ResponseDependency] = Json.format[ResponseDependency]

  case class InitialStateResponse(responseCode: Int, responseMessage: String, data: JsObject)
  implicit val initialStateResponseFormat: OFormat[InitialStateResponse] = Json.format[InitialStateResponse]
}
