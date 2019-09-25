package helpers

import java.util.Date

import play.api.libs.json.{Json, OFormat}
import models.Movimientos
import utils.Constants._
import utils.UniqueId

object UsersControllerHelper {


  case class ResponseModelMovements(id: Option[String],
                               movimiento: Option[Int],
                               numTram: Option[String],
                               estadoDocumento: String,
                               documentosInternosId: Option[String],
                               dependenciasId: String,
                               dependenciasId1: String,
                               asignadoA: Option[String],
                               usuarioId: String,
                               fechaIngreso: Option[String],
                               fechaEnvio: Option[String],
                               observacion: Option[String],
                               indiNombre: Option[String],
                               indiCod: Option[String])

  implicit val responseModelMovementsFormat: OFormat[ResponseModelMovements] = Json.format[ResponseModelMovements]

  case class ResponseMovements(responseCode: Int, responseMessage: String, data: Seq[ResponseModelMovements])
  implicit val responseMovementsFormat: OFormat[ResponseMovements] = Json.format[ResponseMovements]

  def toResponseMovements(movimiento: Movimientos) : ResponseModelMovements ={
    val response = ResponseModelMovements(movimiento.id,movimiento.movimiento,movimiento.numTram,movimiento.estadoDocumento,movimiento.documentosInternosId,movimiento.dependenciasId,
      movimiento.dependenciasId1,movimiento.asignadoA,movimiento.usuarioId,
      Option(convertToString(movimiento.fechaIngreso)),Option(convertToString(movimiento.fechaEnvio)),movimiento.observacion,movimiento.indiNombre,movimiento.indiCod)
    response
  }

  case class RequestUpdateMovements(userId: String, movementsIds: Seq[String], currentDate: String, asignadoA: String)
  implicit val requestUpdateMovementsFormat: OFormat[RequestUpdateMovements] = Json.format[RequestUpdateMovements]

  case class RequestMovements(movimiento: Option[Int],
                               numTram: Option[String],
                               estadoDocumento: String,
                               destinyId: String,
                               fechaIngreso: Option[String],
                               fechaEnvio: Option[String],
                               observacion: Option[String],
                               indiNombre: Option[String],
                               indiCod: Option[String])
  implicit val requestMovementsFormat: OFormat[RequestMovements] = Json.format[RequestMovements]

  case class RequestDeriveMovements(userId: String, officeId: String, movements: Seq[RequestMovements]) {

    def toMovementsModel: Seq[Movimientos] = {
      val newMovements = movements.map( move => {
        val movementId = UniqueId.generateId
        val movement = Movimientos(Some(movementId),Some(move.movimiento.get +1),move.numTram,"DERIVADO",Some(""),move.destinyId,officeId,Some(""),
          userId,None,Some(new java.sql.Timestamp(new Date().getTime)),move.observacion,move.indiNombre,move.indiCod,
          Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))
        movement
      })
      newMovements
    }
  }

  implicit val requestDeriveMovements: OFormat[RequestDeriveMovements] = Json.format[RequestDeriveMovements]
}
