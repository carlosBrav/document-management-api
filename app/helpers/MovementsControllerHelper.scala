package helpers

import java.util.Date

import helpers.DocumentInternControllerHelper.RequestModelDocumentosInternos
import models.{Dependencias, DocumentosInternos, Movimientos}
import play.api.libs.json.{Json, OFormat}
import utils.Constants.convertToString
import utils.UniqueId

object MovementsControllerHelper {

  case class ResponseModelMovements(id: Option[String],
                                    movimiento: Option[Int],
                                    numTram: Option[String],
                                    estadoDocumento: String,
                                    documentosInternosId: Option[String],
                                    dependenciasId: Option[String],
                                    origenNombre: Option[String],
                                    dependenciasId1: Option[String],
                                    destinoNombre: Option[String],
                                    asignadoA: Option[String],
                                    usuarioId: String,
                                    fechaIngreso: Option[String],
                                    fechaEnvio: Option[String],
                                    observacion: Option[String],
                                    indiNombre: Option[String],
                                    indiCod: Option[String],
                                    docuNombre: Option[String],
                                    docuNum: Option[String],
                                    docuSiglas: Option[String],
                                    docuAnio: Option[String])

  implicit val responseModelMovementsFormat: OFormat[ResponseModelMovements] = Json.format[ResponseModelMovements]

  case class ResponseMovements(responseCode: Int, responseMessage: String, data: Seq[ResponseModelMovements])
  implicit val responseMovementsFormat: OFormat[ResponseMovements] = Json.format[ResponseMovements]

  def toResponseMovements(movimiento: Movimientos, dependencyOrigin: Option[Dependencias], dependencyDestiny: Option[Dependencias]) : ResponseModelMovements ={
    val response = ResponseModelMovements(movimiento.id,
      movimiento.movimiento,
      movimiento.numTram,
      movimiento.estadoDocumento,
      movimiento.documentosInternosId,
      Some(movimiento.dependenciasId),
      Some(dependencyOrigin.get.nombre),
      Some(movimiento.dependenciasId1),
      Some(dependencyDestiny.get.nombre),
      movimiento.asignadoA,
      movimiento.usuarioId,
      Option(convertToString(movimiento.fechaIngreso)),
      Option(convertToString(movimiento.fechaEnvio)),
      movimiento.observacion,movimiento.indiNombre,
      movimiento.indiCod,
      movimiento.docuNombre,
      movimiento.docuNum,
      movimiento.docuSiglas,
      movimiento.docuAnio)
    response
  }

  case class RequestUpdateMovements(userId: String, movementsIds: Seq[String], currentDate: String, asignadoA: String)
  implicit val requestUpdateMovementsFormat: OFormat[RequestUpdateMovements] = Json.format[RequestUpdateMovements]

  case class RequestMovements(
                               movimiento: Option[Int],
                               numTram: Option[String],
                               estadoDocumento: String,
                               destinyId: String,
                               fechaIngreso: Option[String],
                               fechaEnvio: Option[String],
                               observacion: Option[String],
                               indiNombre: Option[String],
                               indiCod: Option[String],
                               docuNombre: Option[String],
                               docuNum: Option[String],
                               docuSiglas: Option[String],
                               docuAnio: Option[String])

  implicit val requestMovementsFormat: OFormat[RequestMovements] = Json.format[RequestMovements]

  case class RequestDeriveMovements(userId: String, officeId: String, movements: Seq[RequestMovements]) {

    def toMovementsModel: Seq[Movimientos] = {
      val newMovements = movements.map( move => {
        val movementId = UniqueId.generateId
        val movement = Movimientos(Some(movementId),Some(move.movimiento.get +1),move.numTram,"DERIVADO",Some(""),move.destinyId,officeId,Some(""),
          userId,None,Some(new java.sql.Timestamp(new Date().getTime)),move.observacion,move.indiNombre,move.indiCod,
          move.docuNombre, move.docuNum, move.docuSiglas, move.docuAnio,
          Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))
        movement
      })
      newMovements
    }
  }

  implicit val requestDeriveMovements: OFormat[RequestDeriveMovements] = Json.format[RequestDeriveMovements]

  case class RequestResponseToMovements(documentoInterno: RequestModelDocumentosInternos,
                                        movement: RequestMovements) {

    def toMovementModel(userId: String, officeId: String): (DocumentosInternos, Movimientos) = {


      val documentInternoId = UniqueId.generateId
      val newDocumentIntern = DocumentosInternos(Some(documentInternoId),
        documentoInterno.estado,
        documentoInterno.tipoDocuId,
        documentoInterno.numDocumento,
        documentoInterno.siglas,
        documentoInterno.anio,
        documentoInterno.asunto,
        documentoInterno.observacion,
        documentoInterno.dependenciaId,
        documentoInterno.active,
        Some(new java.sql.Timestamp(new Date().getTime)),
        Some(new java.sql.Timestamp(new Date().getTime)))

      val movementId = UniqueId.generateId
      val newMovement = Movimientos(Some(movementId),Some(movement.movimiento.get +1),movement.numTram,
        "DERIVADO",Some(documentInternoId),movement.destinyId,officeId,Some(""), userId,None,Some(new java.sql.Timestamp(new Date().getTime)),
        movement.observacion,movement.indiNombre,movement.indiCod, movement.docuNombre,movement.docuNum,movement.docuSiglas, movement.docuAnio,
        Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))
      (newDocumentIntern,newMovement)
    }
  }

  implicit val requestResponseToMovementsFormat: OFormat[RequestResponseToMovements] =
    Json.format[RequestResponseToMovements]
}
