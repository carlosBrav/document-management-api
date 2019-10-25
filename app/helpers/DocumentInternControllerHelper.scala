package helpers

import play.api.libs.json.{Json, OFormat}
import models.{DocumentosInternos, Movimientos}
import java.util._

import helpers.MovementsControllerHelper.{RequestModelMovements,ResponseModelMovements}
import utils.Constants._
import utils.UniqueId

object DocumentInternControllerHelper {

  case class RequestResponseModelDocInt(
                                        estadoDocumento: Option[String],
                                        tipoDocuId: String,
                                        numDocumento: Option[Int],
                                        siglas: Option[String],
                                        anio: Option[String],
                                        observacion: Option[String],
                                        asunto: Option[String],
                                        dependenciaId: String,
                                        userId: Option[String],
                                        firma: Option[String],
                                        active: Boolean)
  implicit val requestResponseModelDocIntFormat: OFormat[RequestResponseModelDocInt] =
    Json.format[RequestResponseModelDocInt]

  case class RequestModelDocumentosInternos(
                                             tipoDocuId: String,
                                             numDocumento: Option[Int],
                                             siglas: Option[String],
                                             anio: Option[String],
                                             asunto: Option[String],
                                             dependenciaId: String,
                                             userId: Option[String],
                                             firma: Option[String])

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
                                       userId: Option[String],
                                       firma: Option[String],
                                       fechaCreacion: Option[String],
                                       fechaModificacion: Option[String],
                                     )
  implicit val responseDocumentsInternsFormat: OFormat[ResponseDocumentsInterns] = Json.format[ResponseDocumentsInterns]

  def toResponseDocumentsInterns(tipoDocuId: Option[String], siglas: Option[String], documents: Option[DocumentosInternos]) : ResponseDocumentsInterns = {
    val document = documents.getOrElse(DocumentosInternos(Some(""),
      Some(""), "",Some(-1),siglas,Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",true,Some(""),Some(""),None,None))

    ResponseDocumentsInterns(document.id,
      document.estadoDocumento,document.tipoDocuId,Some(document.numDocumento.get),document.siglas,document.anio,
      document.asunto, document.observacion,document.dependenciaId,document.active, document.userId, document.firma,
      Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)))
  }

  case class SubResponseCircular(documentIntern: ResponseDocumentsInterns, movements: Seq[ResponseModelMovements])
  implicit val subResponseCircularFormat: OFormat[SubResponseCircular] = Json.format[SubResponseCircular]

  case class ResponseCircularDocuments(responseCode: Int, data: Seq[SubResponseCircular])
  implicit val responseCircularDocumentsFormat: OFormat[ResponseCircularDocuments] = Json.format[ResponseCircularDocuments]

  case class ResponseAllDocumentsInterns(responseCode: Int, responseMessage: String, documents: Seq[ResponseDocumentsInterns])
  implicit val responseAllDocumentsInternsFormat: OFormat[ResponseAllDocumentsInterns] = Json.format[ResponseAllDocumentsInterns]

  case class RequestCreateCircular(documentIntern: RequestModelDocumentosInternos,
                                   destinations: Seq[String]) {

    def toModels(userId: String, officeId: String): (DocumentosInternos, Seq[Movimientos]) = {


      val documentInternoId = UniqueId.generateId
      val newDocumentIntern = DocumentosInternos(Some(documentInternoId),
        Some("GENERADO"),
        documentIntern.tipoDocuId,
        documentIntern.numDocumento,
        documentIntern.siglas,
        documentIntern.anio,
        documentIntern.asunto,
        Some(""),
        documentIntern.dependenciaId,
        true,
        documentIntern.userId,
        documentIntern.firma,
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
