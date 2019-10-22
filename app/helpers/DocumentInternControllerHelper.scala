package helpers

import play.api.libs.json.{Json, OFormat}
import models.{DocumentosInternos, Movimientos}
import java.util._

import helpers.MovementsControllerHelper.RequestModelMovements
import utils.Constants._
import utils.UniqueId

object DocumentInternControllerHelper {

  case class RequestModelDocumentosInternos(
                                             estadoDocumento: Option[String],
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
      document.estadoDocumento,document.tipoDocuId,Some(document.numDocumento.get),document.siglas,document.anio,
      document.asunto, document.observacion,document.dependenciaId,document.active,
      Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)))
  }

  case class ResponseAllDocumentsInterns(responseCode: Int, responseMessage: String, documents: Seq[ResponseDocumentsInterns])
  implicit val responseAllDocumentsInternsFormat: OFormat[ResponseAllDocumentsInterns] = Json.format[ResponseAllDocumentsInterns]

  case class RequestCreateCircular(documentIntern: RequestModelDocumentosInternos,
                                   destinations: Seq[String]) {

    def toModels(userId: String, officeId: String): (DocumentosInternos, Seq[Movimientos]) = {


      val documentInternoId = UniqueId.generateId
      val newDocumentIntern = DocumentosInternos(Some(documentInternoId),
        documentIntern.estadoDocumento,
        documentIntern.tipoDocuId,
        documentIntern.numDocumento,
        documentIntern.siglas,
        documentIntern.anio,
        documentIntern.asunto,
        documentIntern.observacion,
        documentIntern.dependenciaId,
        documentIntern.active,
        Some(new java.sql.Timestamp(new Date().getTime)),
        Some(new java.sql.Timestamp(new Date().getTime)))

      val newMovements = destinations.map(destination => {
        val movementId = UniqueId.generateId
        val newMovement = Movimientos(Some(movementId), Some(0), Some(movementId),
          "DERIVADO", Some(documentInternoId), officeId , destination, Some(""), userId, None, Some(new java.sql.Timestamp(new Date().getTime)),
          Some(""), Some(""), Some(""), Some(""),Some(""),Some(""), Some(""),
          Some(new java.sql.Timestamp(new Date().getTime)), Some(new java.sql.Timestamp(new Date().getTime)))
        newMovement
      })

      (newDocumentIntern,newMovements)
    }
  }

  implicit val requestCreateCircular: OFormat[RequestCreateCircular] =
    Json.format[RequestCreateCircular]
}
