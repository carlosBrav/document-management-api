package helpers

import play.api.libs.json.{Json, OFormat}
import models.{DocumentosInternos, Movimientos}
import java.util._

import helpers.MovementsControllerHelper.RequestMovements
import utils.Constants._
import utils.UniqueId

object DocumentInternControllerHelper {

  case class RequestModelDocumentosInternos(
                                             estado: Option[String],
                                             tipoDocuId: String,
                                             numDocumento: Option[Int],
                                             siglas: Option[String],
                                             anio: Option[String],
                                             asunto: Option[String],
                                             observacion: Option[String],
                                             dependenciaId: String,
                                             active: Boolean)

  implicit val requestDocumentosInternosFormat: OFormat[RequestModelDocumentosInternos] =
    Json.format[RequestModelDocumentosInternos]

  case class ResponseDocumentsInterns(
                                       id: Option[String],
                                       estado: Option[String],
                                       tipoDocuId: String,
                                       numDocumento: Option[Int],
                                       siglas: Option[String],
                                       anio: Option[String],
                                       asunto: Option[String],
                                       observacion: Option[String],
                                       dependenciaId: String,
                                       active: Boolean,
                                       fechaCreacion: Option[String],
                                       fechaModificacion: Option[String],
                                     )
  implicit val responseDocumentsInternsFormat: OFormat[ResponseDocumentsInterns] = Json.format[ResponseDocumentsInterns]

  def toResponseDocumentsInterns(tipoDocuId: Option[String], siglas: Option[String], documents: Option[DocumentosInternos]) : ResponseDocumentsInterns = {
    val document = documents.getOrElse(DocumentosInternos(Some(""),
      Some(""), "",Some(-1),siglas,Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",true,None,None))

    ResponseDocumentsInterns(document.id,
      document.estado,document.tipoDocuId,Some(document.numDocumento.get+1),document.siglas,document.anio,
      document.asunto, document.observacion,document.dependenciaId,document.active,
      Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)))
  }

  case class ResponseAllDocumentsInterns(responseCode: Int, responseMessage: String, documents: Seq[ResponseDocumentsInterns])
  implicit val responseAllDocumentsInternsFormat: OFormat[ResponseAllDocumentsInterns] = Json.format[ResponseAllDocumentsInterns]

  case class RequestCreateCircular(documentoInterno: RequestModelDocumentosInternos,
                                        movements: Seq[RequestMovements]) {

    def toModels(userId: String, officeId: String): (DocumentosInternos, Seq[Movimientos]) = {


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

      val newMovements = movements.map(move => {
        val movementId = UniqueId.generateId
        val newMovement = Movimientos(Some(movementId), Some(move.movimiento.get), move.numTram,
          "DERIVADO", Some(documentInternoId), move.destinyId, officeId, Some(""), userId, None, Some(new java.sql.Timestamp(new Date().getTime)),
          move.observacion, move.indiNombre, move.indiCod,
          Some(new java.sql.Timestamp(new Date().getTime)), Some(new java.sql.Timestamp(new Date().getTime)))
        newMovement
      })

      (newDocumentIntern,newMovements)
    }
  }

  implicit val requestCreateCircular: OFormat[RequestCreateCircular] =
    Json.format[RequestCreateCircular]
}
