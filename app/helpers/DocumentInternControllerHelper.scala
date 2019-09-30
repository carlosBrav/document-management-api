package helpers

import play.api.libs.json.{Json, OFormat}
import models.DocumentosInternos
import java.util._
import utils.Constants._

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

  def toResponseDocumentsInterns(tipoDocuId: String, siglas: String, documents: Option[DocumentosInternos]) : ResponseDocumentsInterns = {
    val document = documents.getOrElse(DocumentosInternos(Some(""),
      Some(""), "",Some(-1),Some(siglas),Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",true,None,None))

    ResponseDocumentsInterns(document.id,
      document.estado,document.tipoDocuId,Some(document.numDocumento.get+1),document.siglas,document.anio,
      document.asunto, document.observacion,document.dependenciaId,document.active,
      Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)))
  }
}
