package helpers

import play.api.libs.json.{Json, OFormat}
import models.Movimientos
import utils.Constants._

object UsersControllerHelper {


  case class ResponseModelMovements(id: Option[String],
                               movimiento: Option[Int],
                               numTram: Option[String],
                               estadoDocumento: String,
                               estadoConfirmacion: Boolean,
                               documentosInternosId: Option[String],
                               dependenciasId: String,
                               dependenciasId1: String,
                               asignadoA: Option[String],
                               usuarioId: String,
                               fechaIngreso: Option[String],
                               fechaDerivacion: Option[String],
                               fechaEnvio: Option[String],
                               observacion: Option[String],
                               indiNombre: Option[String],
                               indiCod: Option[String])

  implicit val responseModelMovementsFormat: OFormat[ResponseModelMovements] = Json.format[ResponseModelMovements]

  case class ResponseMovements(responseCode: Int, responseMessage: String, data: Seq[ResponseModelMovements])
  implicit val responseMovementsFormat: OFormat[ResponseMovements] = Json.format[ResponseMovements]

  def toResponseMovements(movimiento: Movimientos) : ResponseModelMovements ={
    val response = ResponseModelMovements(movimiento.id,movimiento.movimiento,movimiento.numTram,movimiento.estadoDocumento,movimiento.estadoConfirmacion,movimiento.documentosInternosId,movimiento.dependenciasId,
      movimiento.dependenciasId1,movimiento.asignadoA,movimiento.usuarioId,
      Option(convertToString(movimiento.fechaIngreso)),None,Option(convertToString(movimiento.fechaEnvio)),movimiento.observacion,movimiento.indiNombre,movimiento.indiCod)
    response
  }

  case class RequestUpdateMovements(userId: String, movementsIds: Seq[String])
  implicit val requestUpdateMovementsFormat: OFormat[RequestUpdateMovements] = Json.format[RequestUpdateMovements]
}
