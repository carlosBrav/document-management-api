package helpers
import java.util.Date

import play.api.libs.json.{Json, OFormat}
import models.{Dependencias, Rol, Usuario}
import utils.Constants.convertToDate
import utils.{BCryptPass, Format, UniqueId}

object UsersControllerHelper {

  case class RequestCreateUser(
                                usuario: String,
                                password: String,
                                estado: Boolean = true,
                                rolId: String,
                                nombre: String,
                                apellido: String,
                                telefono: Option[String],
                                dependenciaId: Option[String],
                                isSubOfficeBoss: Boolean = false,
                                isOfficeBoss: Boolean = false,
                                email: String
                              )
  implicit val requestCreateUserFormat: OFormat[RequestCreateUser] = Json.format[RequestCreateUser]

  case class RequestUpdateUser(
                                id: Option[String],
                                usuario: String,
                                estado: Boolean = true,
                                rolId: String,
                                nombre: String,
                                apellido: String,
                                telefono: Option[String],
                                dependenciaId: Option[String],
                                isSubOfficeBoss: Boolean = false,
                                isOfficeBoss: Boolean = false,
                                email: String
                              )
  implicit val requestUpdateUserFormat: OFormat[RequestUpdateUser] = Json.format[RequestUpdateUser]

  case class UpdateUser(user: RequestUpdateUser)
  implicit val updateUser: OFormat[UpdateUser] = Json.format[UpdateUser]

  case class maxCorrelativeRequest(officeId: String, typeDocumentId: String, siglas: String)
  implicit val maxCorrelativeRequestFormat: OFormat[maxCorrelativeRequest] = Json.format[maxCorrelativeRequest]

  case class maxCorrelativeResponse(documentNumber: String, documentSiglas: String, documentYear: String)
  implicit val maxCorrelativeResponseFormat: OFormat[maxCorrelativeResponse] = Json.format[maxCorrelativeResponse]

  case class userModelResponse(id: Option[String],
                          usuario: String,
                          estado: Boolean,
                          rolId: String,
                          rolName: String,
                          nombre: String,
                          apellido: String,
                          telefono: Option[String],
                          dependenciaId: Option[String],
                          officeName: String,
                          isSubOfficeBoss: Boolean,
                          isOfficeBoss: Boolean,
                          email: String)

  implicit val userModelResponseFormat: OFormat[userModelResponse] = Json.format[userModelResponse]

  case class userSimpleModelResponse(id: Option[String],
                          usuario: String,
                          estado: Boolean,
                          rolId: String,
                          nombre: String,
                          apellido: String,
                          telefono: Option[String],
                          dependenciaId: Option[String],
                          isSubOfficeBoss: Boolean,
                          isOfficeBoss: Boolean,
                          email: String)

  implicit val userSimpleModelResponseFormat: OFormat[userSimpleModelResponse] = Json.format[userSimpleModelResponse]

  case class userResponse(responseCode: Int, data: userModelResponse)
  implicit val userResponseFormat: OFormat[userResponse] = Json.format[userResponse]

  case class userSimpleResponse(responseCode: Int, data: userSimpleModelResponse)
  implicit val userSimpleResponseFormat: OFormat[userSimpleResponse] = Json.format[userSimpleResponse]

  def toUserModel(user: Usuario, rol: Option[Rol], office: Option[Dependencias]) = {
    userModelResponse(
      user.id,
      user.usuario,
      user.estado,
      user.rolId,
      rol.get.nombre,
      user.nombre,
      user.apellido,
      user.telefono,
      user.dependenciaId,
      office.get.nombre,
      user.isSubOfficeBoss,
      user.isOfficeBoss,
      user.email)
  }

  def toUserSimpleModel(user: Usuario) = {
    userSimpleModelResponse(
      user.id,
      user.usuario,
      user.estado,
      user.rolId,
      user.nombre,
      user.apellido,
      user.telefono,
      user.dependenciaId,
      user.isSubOfficeBoss,
      user.isOfficeBoss,
      user.email)
  }

  def toNewUser(requestUser: RequestCreateUser) = {
    val userId = UniqueId.generateId
    Usuario(Some(userId),
      requestUser.usuario,
      BCryptPass.createHashPass(requestUser.password),
      true,
      requestUser.rolId,
      requestUser.nombre,
      requestUser.apellido,
      requestUser.telefono,
      requestUser.dependenciaId,
      requestUser.isSubOfficeBoss,
      requestUser.isOfficeBoss,
      requestUser.email,
      Some(new java.sql.Timestamp(new Date().getTime)),
      Some(new java.sql.Timestamp(new Date().getTime))
    )
  }

  case class ListUserResponse(responseCode: Int, users: Seq[userModelResponse])
  implicit val listUserResponseFormat: OFormat[ListUserResponse] = Json.format[ListUserResponse]
}
