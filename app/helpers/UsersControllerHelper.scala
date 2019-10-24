package helpers
import play.api.libs.json.{Json, OFormat}
import models.Usuario

object UsersControllerHelper {

  case class maxCorrelativeRequest(officeId: String, typeDocumentId: String, siglas: String)
  implicit val maxCorrelativeRequestFormat: OFormat[maxCorrelativeRequest] = Json.format[maxCorrelativeRequest]

  case class maxCorrelativeResponse(documentNumber: String, documentSiglas: String, documentYear: String)
  implicit val maxCorrelativeResponseFormat: OFormat[maxCorrelativeResponse] = Json.format[maxCorrelativeResponse]

  case class userModelResponse(id: Option[String],
                          usuario: String,
                          estado: Boolean,
                          rolId: String,
                          nombre: String,
                          apellido: String,
                          telefono: Option[String],
                          dependenciaId: Option[String],
                          isSubOfficeBoss: Boolean,
                          isOfficeBoss: Boolean)

  implicit val userModelResponseFormat: OFormat[userModelResponse] = Json.format[userModelResponse]

  case class userResponse(responseCode: Int, data: userModelResponse)
  implicit val userResponseFormat: OFormat[userResponse] = Json.format[userResponse]

  def toUserModel(user: Usuario) = {
    userModelResponse(user.id,user.usuario,user.estado,user.rolId,user.nombre,user.apellido,user.telefono,user.dependenciaId,user.isSubOfficeBoss,user.isOfficeBoss)
  }
}
