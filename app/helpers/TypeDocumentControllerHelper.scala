package helpers

import play.api.libs.json.{Json, OFormat}
import models.{TipoDocumento}

object TypeDocumentControllerHelper {

  case class ResponseModelTypeDocument(
                                  id: Option[String],
                                  nombreTipo: String,
                                  flag1: Option[String],
                                  flag2: Option[String])

  implicit val responseModelTypeDocumentFormat: OFormat[ResponseModelTypeDocument] = Json.format[ResponseModelTypeDocument]

  case class ResponseTypeDocuments(responseCode: Int, responseMessage: String, data: Seq[ResponseModelTypeDocument])
  implicit val responseTypeDocumentsFormat: OFormat[ResponseTypeDocuments] = Json.format[ResponseTypeDocuments]

  def toModelTypeDocuments(typeDocument: TipoDocumento) = {
    val modelTypeDocument = ResponseModelTypeDocument(typeDocument.id,typeDocument.nombreTipo,typeDocument.flag1,typeDocument.flag2)
    modelTypeDocument
  }
}
