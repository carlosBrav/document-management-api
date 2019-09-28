package helpers

import play.api.libs.json.{Json, OFormat}
import models.DocumentosInternos
import java.util._
import utils.Constants._

object DocumentInternControllerHelper {

  case class RequestModelDocumentosInternos(
                                             estado: Boolean,
                                             tipoDocuId: String,
                                             numDocumento: Option[Int],
                                             siglas: Option[String],
                                             anio: Option[String],
                                             asunto: Option[String],
                                             dependenciaId: String)

  implicit val requestDocumentosInternosFormat: OFormat[RequestModelDocumentosInternos] =
    Json.format[RequestModelDocumentosInternos]

  case class ResponseDocumentsInterns(
                                       id: Option[String],
                                       estado: Boolean,
                                       tipoDocuId: String,
                                       numDocumento: Option[Int],
                                       siglas: Option[String],
                                       anio: Option[String],
                                       asunto: Option[String],
                                       dependenciaId: String,
                                       fechaCreacion: Option[String],
                                       fechaModificacion: Option[String],
                                     )
  implicit val responseDocumentsInternsFormat: OFormat[ResponseDocumentsInterns] = Json.format[ResponseDocumentsInterns]

  def toResponseDocumentsInterns(tipoDocuId: String, siglas: String, documents: Option[DocumentosInternos]) : ResponseDocumentsInterns = {
    val document = documents.getOrElse(DocumentosInternos(Some(""), false, "",Some(-1),Some(siglas),Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),"",None,None))

    ResponseDocumentsInterns(document.id,document.estado,document.tipoDocuId,Some(document.numDocumento.get+1),document.siglas,document.anio,
      document.asunto,document.dependenciaId,Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)))
  }
}
