package helpers

import java.util.Date

import helpers.DocumentInternControllerHelper.RequestResponseModelDocInt
import models.{Dependencias, DocumentosInternos, Movimientos}
import play.api.libs.json.{Json, OFormat}
import utils.Constants.{convertToString,convertToDate}
import utils._
import utils.UniqueId

object MovementsControllerHelper {

  case class RequestDeleteMovements(movementsIds: Seq[String])
  implicit val requestDeleteMovementsFormat: OFormat[RequestDeleteMovements] = Json.format[RequestDeleteMovements]

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
                                    docuNombre: String,
                                    docuNum: String,
                                    docuSiglas: String,
                                    docuAnio: String)

  implicit val responseModelMovementsFormat: OFormat[ResponseModelMovements] = Json.format[ResponseModelMovements]

  case class ResponseMovements(responseCode: Int, responseMessage: String, data: Seq[ResponseModelMovements])
  implicit val responseMovementsFormat: OFormat[ResponseMovements] = Json.format[ResponseMovements]

  def toResponseDetailsMovements(movimiento: Movimientos, dependencyDestiny: Option[Dependencias]) = {
    val response = ResponseModelMovements(movimiento.id,
      movimiento.movimiento,
      movimiento.numTram,
      movimiento.estadoDocumento,
      movimiento.documentosInternosId,
      Some(movimiento.dependenciasId),
      Some(""),
      Some(movimiento.dependenciasId1),
      Some(dependencyDestiny.get.nombre),
      movimiento.asignadoA,
      movimiento.usuarioId,
      Option(convertToString(movimiento.fechaIngreso)),
      Option(convertToString(movimiento.fechaEnvio)),
      movimiento.observacion,movimiento.indiNombre,
      movimiento.indiCod,
      movimiento.docuNombre.getOrElse(""),
      movimiento.docuNum.getOrElse(""),
      movimiento.docuSiglas.getOrElse(""),
      movimiento.docuAnio.getOrElse(""))
    response
  }

  def toResponseModelMovements(movimiento: Movimientos) = {
    val response = ResponseModelMovements(movimiento.id,
      movimiento.movimiento,
      movimiento.numTram,
      movimiento.estadoDocumento,
      movimiento.documentosInternosId,
      Some(movimiento.dependenciasId),
      Some(""),
      Some(movimiento.dependenciasId1),
      Some(""),
      movimiento.asignadoA,
      movimiento.usuarioId,
      Option(convertToString(movimiento.fechaIngreso)),
      Option(convertToString(movimiento.fechaEnvio)),
      movimiento.observacion,movimiento.indiNombre,
      movimiento.indiCod,
      movimiento.docuNombre.getOrElse(""),
      movimiento.docuNum.getOrElse(""),
      movimiento.docuSiglas.getOrElse(""),
      movimiento.docuAnio.getOrElse(""))
    response
  }

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
      movimiento.docuNombre.getOrElse(""),
      movimiento.docuNum.getOrElse(""),
      movimiento.docuSiglas.getOrElse(""),
      movimiento.docuAnio.getOrElse(""))
    response
  }

  case class RequestUpdateMovements(userId: String, movementsIds: Seq[String], currentDate: String, asignadoA: String)
  implicit val requestUpdateMovementsFormat: OFormat[RequestUpdateMovements] = Json.format[RequestUpdateMovements]

  case class RequestModelMovements(id: Option[String],
                                   movimiento: Option[Int],
                                   numTram: Option[String],
                                   estadoDocumento: String,
                                   dependenciasId1: String,
                                   fechaIngreso: Option[String],
                                   fechaEnvio: Option[String],
                                   observacion: Option[String],
                                   indiNombre: Option[String],
                                   indiCod: Option[String],
                                   docuNombre: Option[String],
                                   docuNum: Option[String],
                                   docuSiglas: Option[String],
                                   docuAnio: Option[String],
                                   currentDate: Option[String])

  implicit val requestMovementsFormat: OFormat[RequestModelMovements] = Json.format[RequestModelMovements]

  case class RequestDeriveMovements(userId: String, officeId: String, currentDate: String, movements: Seq[RequestModelMovements]) {

    def toMovementsModel: Seq[Movimientos] = {
      val newMovements = movements.map( move => {
        val movementId = UniqueId.generateId
        val movement = Movimientos(Some(movementId),Some(move.movimiento.get +1),move.numTram,"EN PROCESO",Some(""),move.dependenciasId1,officeId,Some(""),
          userId,None,Some(new java.sql.Timestamp(new Date().getTime)),move.observacion,move.indiNombre,move.indiCod,
          move.docuNombre, move.docuNum, move.docuSiglas, move.docuAnio,
          Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))
        movement
      })
      newMovements
    }
  }

  implicit val requestDeriveMovements: OFormat[RequestDeriveMovements] = Json.format[RequestDeriveMovements]

  case class RequestResponseToMovements(documentIntern: RequestResponseModelDocInt,
                                        movement: RequestModelMovements) {

    def toMovementModel(userId: String, officeId: String): (DocumentosInternos, Movimientos) = {


      val documentInternId = UniqueId.generateId
      val newDocumentIntern = DocumentosInternos(Some(documentInternId),
        documentIntern.estadoDocumento,
        documentIntern.tipoDocuId,
        documentIntern.numDocumento,
        documentIntern.siglas,
        documentIntern.anio,
        documentIntern.asunto,
        documentIntern.observacion,
        documentIntern.dependenciaId,
        documentIntern.active,
        documentIntern.userId,
        documentIntern.firma,
        Some(new java.sql.Timestamp(convertToDate(documentIntern.currentDate.get, Format.LOCAL_DATE).getTime)),
        Some(new java.sql.Timestamp(convertToDate(documentIntern.currentDate.get, Format.LOCAL_DATE).getTime)))

      val movementId = UniqueId.generateId
      val newMovement = Movimientos(Some(movementId),
        Some(movement.movimiento.get +1),
        movement.numTram,
        "DERIVADO",
        Some(documentInternId),
        movement.dependenciasId1,
        officeId,
        Some(""),
        userId,None,
        Some(new java.sql.Timestamp(new Date().getTime)),
        movement.observacion,
        movement.indiNombre,
        movement.indiCod,
        movement.docuNombre,
        movement.docuNum,
        movement.docuSiglas,
        movement.docuAnio,
        Some(new java.sql.Timestamp(convertToDate(movement.currentDate.get, Format.LOCAL_DATE).getTime)),
        Some(new java.sql.Timestamp(convertToDate(movement.currentDate.get, Format.LOCAL_DATE).getTime)))
      (newDocumentIntern,newMovement)
    }
  }
  implicit val requestResponseToMovementsFormat: OFormat[RequestResponseToMovements] =
    Json.format[RequestResponseToMovements]

  case class ResponseCircularDetails(responseCode: Int, data: Seq[ResponseModelMovements])
  implicit val responseCircularDetailsFormat: OFormat[ResponseCircularDetails] = Json.format[ResponseCircularDetails]
}
