package helpers
import play.api.libs.json.{Json, OFormat}

object UsersControllerHelper {

  case class maxCorrelativeRequest(officeId: String, typeDocumentId: String, siglas: String)
  implicit val maxCorrelativeRequestFormat: OFormat[maxCorrelativeRequest] = Json.format[maxCorrelativeRequest]

  case class maxCorrelativeResponse(documentNumber: String, documentSiglas: String, documentYear: String)
  implicit val maxCorrelativeResponseFormat: OFormat[maxCorrelativeResponse] = Json.format[maxCorrelativeResponse]
}
