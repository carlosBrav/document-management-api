package helpers

import java.util.{Calendar, Date}

import helpers.DocumentInternControllerHelper.RequestResponseModelDocInt
import models.{Dependencias, DocumentosInternos, Movimientos, TipoDocumento}
import play.api.libs.json.{Json, OFormat}
import utils.Constants.{convertToDate, convertToString}
import utils._
import utils.UniqueId

object MovementsControllerHelper {

  case class RequestAdvancedSearch(numTram: Option[String], observation: Option[String], officeId: Option[String])
  implicit val requestAdvancedSearchFormat: OFormat[RequestAdvancedSearch] =  Json.format[RequestAdvancedSearch]

  case class ResponseModelMovement(id: Option[String],
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
                                    docuAnio: String,
                                    previousMovementId: Option[String])

  implicit val responseModelMovementFormat: OFormat[ResponseModelMovement] = Json.format[ResponseModelMovement]

  case class ResponseAdminMovements(id: Option[String],
                                    movimiento: Option[Int],
                                    numTram: Option[String],
                                    estadoDocumento: String,
                                    documentosInternosId: Option[String],
                                    dependenciasId: Option[String],
                                    origenNombre: Option[String],
                                    fechaEnvio: Option[String],
                                    observacion: Option[String],
                                    indiNombre: Option[String],
                                    docuNombre: String,
                                    docuNum: String,
                                    docuSiglas: String,
                                    docuAnio: String,
                                    numDocumentIntern: Option[String],
                                    siglasDocumentIntern: Option[String],
                                    anioDocumentIntern: Option[String],
                                    tipoDocuId: Option[String],
                                    previousMovementId: Option[String]
                                   )
  implicit val responseAdminMovementsFormat: OFormat[ResponseAdminMovements] = Json.format[ResponseAdminMovements]

  case class RequestDeleteMovements(movementsIds: Seq[String])
  implicit val requestDeleteMovementsFormat: OFormat[RequestDeleteMovements] = Json.format[RequestDeleteMovements]

  case class ResponseMovements(responseCode: Int, responseMessage: String, data: Seq[ResponseModelMovement])
  implicit val responseMovementsFormat: OFormat[ResponseMovements] = Json.format[ResponseMovements]

  case class ResponseAdminMovement(responseCode: Int, responseMessage: String, data: Seq[ResponseAdminMovements])
  implicit val responseAdminMovementFormat: OFormat[ResponseAdminMovement] = Json.format[ResponseAdminMovement]

  def toResponseDetailsMovements(movement: Movimientos, dependencyDestiny: Option[Dependencias]) = {
    val response = ResponseModelMovement(movement.id,
      movement.movimiento,
      movement.numTram,
      movement.estadoDocumento,
      movement.documentosInternosId,
      Some(movement.dependenciasId),
      Some(""),
      Some(movement.dependenciasId1),
      Some(dependencyDestiny.get.nombre),
      movement.asignadoA,
      movement.usuarioId,
      Option(convertToString(movement.fechaIngreso)),
      Option(convertToString(movement.fechaEnvio)),
      movement.observacion,movement.indiNombre,
      movement.indiCod,
      movement.docuNombre.getOrElse(""),
      movement.docuNum.getOrElse(""),
      movement.docuSiglas.getOrElse(""),
      movement.docuAnio.getOrElse(""),
      movement.previousMovementId)
    response
  }

  def toResponseModelMovements(movimiento: Movimientos) = {
    val response = ResponseModelMovement(movimiento.id,
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
      movimiento.docuAnio.getOrElse(""),
      movimiento.previousMovementId)
    response
  }

  def toResponseMovements(movimiento: Movimientos,
                          dependencyOrigin: Option[Dependencias],
                          dependencyDestiny: Option[Dependencias]) : ResponseModelMovement ={

    val response = ResponseModelMovement(movimiento.id,
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
      movimiento.docuAnio.getOrElse(""),
      movimiento.previousMovementId)
    response
  }

  def toResponseAdminMovements(movement: Movimientos,
                               dependency: Option[Dependencias],
                               internDocument: Option[DocumentosInternos]
                               ) ={

    val document = internDocument.getOrElse(DocumentosInternos(Some(""),
      Some(""), Some("").getOrElse(""),Some(-1),Some(""),Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",Some(""),Some(""),Some(""),Some(""),Some(""),None,None))

    val response = ResponseAdminMovements(movement.id,
      movement.movimiento,
      movement.numTram,
      movement.estadoDocumento,
      movement.documentosInternosId,
      Some(movement.dependenciasId),
      Some(dependency.get.nombre),
      Option(convertToString(movement.fechaEnvio)),
      movement.observacion,
      movement.indiNombre,
      movement.docuNombre.get,
      movement.docuNum.get,
      movement.docuSiglas.get,
      movement.docuAnio.get,
      Some("%05d".format(document.numDocumento.get)),
      document.siglas,
      document.anio,
      Some(document.tipoDocuId),
      movement.previousMovementId)
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
                                   previousMovementId: Option[String],
                                   currentDate: Option[String])

  implicit val requestMovementsFormat: OFormat[RequestModelMovements] = Json.format[RequestModelMovements]

  case class RequestDeriveMovements(userId: String, officeId: String, currentDate: String, movements: Seq[RequestModelMovements]) {

    def toMovementsModel: Seq[Movimientos] = {
      val newMovements = movements.map( move => {
        val movementId = UniqueId.generateId
        val movement = Movimientos(Some(movementId),
          Some(move.movimiento.get +1),
          move.numTram,
          "EN PROCESO",
          Some(""),
          move.dependenciasId1,
          officeId,
          Some(""),
          userId,
          None,
          Some(new java.sql.Timestamp(new Date().getTime)),
          move.observacion,
          move.indiNombre,
          move.indiCod,
          move.docuNombre,
          move.docuNum,
          move.docuSiglas,
          move.docuAnio,
          move.previousMovementId,
          Some(new java.sql.Timestamp(new Date().getTime)),
          Some(new java.sql.Timestamp(new Date().getTime)))
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
        documentIntern.origenId,
        documentIntern.destinoId,
        documentIntern.userId,
        documentIntern.firma,
        documentIntern.responsableArea,
        documentIntern.referenceDocument,
        Some(new java.sql.Timestamp(convertToDate(documentIntern.currentDate.get, Format.LOCAL_DATE).getTime)),
        Some(new java.sql.Timestamp(convertToDate(documentIntern.currentDate.get, Format.LOCAL_DATE).getTime)))

      val movementId = UniqueId.generateId
      val newMovement = Movimientos(Some(movementId),
        Some(movement.movimiento.get +1),
        movement.numTram,
        movement.estadoDocumento,
        Some(documentInternId),
        officeId,
        movement.dependenciasId1,
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
        movement.id,
        Some(new java.sql.Timestamp(convertToDate(movement.currentDate.get, Format.LOCAL_DATE).getTime)),
        Some(new java.sql.Timestamp(convertToDate(movement.currentDate.get, Format.LOCAL_DATE).getTime)))
      (newDocumentIntern,newMovement)
    }
  }
  implicit val requestResponseToMovementsFormat: OFormat[RequestResponseToMovements] =
    Json.format[RequestResponseToMovements]

  case class ResponseCircularDetails(responseCode: Int, data: Seq[ResponseModelMovement])
  implicit val responseCircularDetailsFormat: OFormat[ResponseCircularDetails] = Json.format[ResponseCircularDetails]
}
