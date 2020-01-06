package helpers
import java.util.Date

import helpers.HomeControllerHelper.ResponseDependency
import play.api.libs.json.{JsObject, Json, OFormat}
import models.Dependencias
import utils.UniqueId

object OfficesControllerHelper {

  case class OfficeModel(
                          id: String,
                          nombre: String,
                          siglas: Option[String],
                          codigo: String,
                          tipo: Option[String],
                          estado: Boolean
                        )
  implicit val officeModel: OFormat[OfficeModel] = Json.format[OfficeModel]

  case class ListOfficesResponse(responseCode: Int, data: Seq[ResponseDependency])
  implicit val listOfficesResponseFormat: OFormat[ListOfficesResponse] = Json.format[ListOfficesResponse]

  case class OfficeModelCreate(
                                nombre: String,
                                siglas: Option[String],
                                codigo: String,
                                tipo: Option[String]
                                )
  implicit val officeModelCreate: OFormat[OfficeModelCreate] = Json.format[OfficeModelCreate]

  case class UpdateOfficeRequest(office: OfficeModel)
  implicit val updateOfficeRequest: OFormat[UpdateOfficeRequest] = Json.format[UpdateOfficeRequest]

  case class CreateOfficeRequest(office: OfficeModelCreate)
  implicit val createOfficeRequest: OFormat[CreateOfficeRequest] = Json.format[CreateOfficeRequest]

  case class OfficeResponseModel(responseCode: Int, office: OfficeModel)
  implicit val officeResponseModel: OFormat[OfficeResponseModel] = Json.format[OfficeResponseModel]

  def toOfficeModel(office: Dependencias) = {
    OfficeModel(office.id.get,office.nombre,office.siglas,office.codigo,Some(office.tipo.getOrElse("-1")),office.estado)
  }

  def toNewOffice(office: OfficeModelCreate) = {
    val officeId = UniqueId.generateId
    Dependencias(Some(officeId),
      office.nombre,
      estado = true,
      office.siglas,
      office.codigo,
      office.tipo,
      Some(new java.sql.Timestamp(new Date().getTime)),
      Some(new java.sql.Timestamp(new Date().getTime)))
  }

}
