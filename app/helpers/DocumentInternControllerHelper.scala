package helpers

import play.api.libs.json.{Json, OFormat}
import models.{DocumentosInternos, Movimientos, TipoDocumento, Usuario, Dependencias}
import java.util._

import helpers.MovementsControllerHelper.{RequestModelMovements,ResponseModelMovements}
import utils.Constants._
import utils.UniqueId

object DocumentInternControllerHelper {

  case class RequestDeleteDocuments(documentsIds: Seq[String])
  implicit val requestDeleteDocumentsFormat: OFormat[RequestDeleteDocuments] = Json.format[RequestDeleteDocuments]

  case class RequestEditCircularDocument(asunto: Option[String],
                                         dependencyId: Option[String])
  implicit val requestEditCircularDocument: OFormat[RequestEditCircularDocument] = Json.format[RequestEditCircularDocument]

  case class RequestResponseModelDocInt(
                                        estadoDocumento: Option[String],
                                        tipoDocuId: String,
                                        numDocumento: Option[Int],
                                        siglas: Option[String],
                                        anio: Option[String],
                                        observacion: Option[String],
                                        asunto: Option[String],
                                        origenId: String,
                                        destinoId: Option[String],
                                        userId: Option[String],
                                        firma: Option[String],
                                        active: Boolean,
                                        currentDate: Option[String])
  implicit val requestResponseModelDocIntFormat: OFormat[RequestResponseModelDocInt] =
    Json.format[RequestResponseModelDocInt]

  case class RequestModelDocumentosInternos(
                                             tipoDocuId: String,
                                             numDocumento: Option[Int],
                                             siglas: Option[String],
                                             anio: Option[String],
                                             asunto: Option[String],
                                             origenId: String,
                                             destinoId: Option[String],
                                             userId: Option[String],
                                             firma: Option[String])

  implicit val requestDocumentosInternosFormat: OFormat[RequestModelDocumentosInternos] =
    Json.format[RequestModelDocumentosInternos]

  case class ResponseDocumentsInterns(
                                       id: Option[String],
                                       estado: Option[String],
                                       tipoDocuId: String,
                                       documentName: Option[String],
                                       numDocumento: Option[String],
                                       siglas: Option[String],
                                       anio: Option[String],
                                       asunto: Option[String],
                                       observacion: Option[String],
                                       origenId: String,
                                       origenName: Option[String],
                                       destinoId: Option[String],
                                       destinoName: Option[String],
                                       active: Boolean,
                                       userId: Option[String],
                                       userName: Option[String],
                                       userLastName: Option[String],
                                       firma: Option[String],
                                       fechaCreacion: Option[String],
                                       fechaModificacion: Option[String],
                                       movementId: Option[String],
                                       numTram: Option[String]
                                     )
  implicit val responseDocumentsInternsFormat: OFormat[ResponseDocumentsInterns] = Json.format[ResponseDocumentsInterns]

  def toResponseDocumentsInterns(tipoDocuId: Option[String],
                                 siglas: Option[String],
                                 documents: Option[DocumentosInternos],
                                 typeDocument: Option[TipoDocumento],
                                 dependency: Option[Dependencias],
                                 dependencyDestiny: Option[Dependencias],
                                 user: Option[Usuario],
                                 movement: Option[Movimientos]) : ResponseDocumentsInterns = {

    val document = documents.getOrElse(DocumentosInternos(Some(""),
      Some(""), tipoDocuId.getOrElse(""),Some(-1),siglas,Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",Some(""),true,Some(""),Some(""),None,None))

    val move = movement.getOrElse(Movimientos(Some(""),Some(0),Some(""),"",Some(""),"","",Some(""),"",None,None,Some(""),Some(""),Some(""),Some(""),Some(""),Some(""),Some(""),None,None))

    val dependencyDest = dependencyDestiny.getOrElse(Dependencias(Some(""),"",false,Some(""),"",Some(""),None,None))

    val response = ResponseDocumentsInterns(document.id,
      document.estadoDocumento,document.tipoDocuId, Some(typeDocument.get.nombreTipo),Some("%05d".format(document.numDocumento.get)),document.siglas,document.anio,
      document.asunto, document.observacion,document.origenId,Some(dependency.get.nombre),document.destinoId,Some(dependencyDest.nombre),document.active, document.userId,
      Some(user.get.nombre), Some(user.get.apellido), document.firma,
      Some(convertToString(document.fechaCreacion)),Some(convertToString(document.fechaModificacion)), move.id,move.numTram)
    response
  }

  case class ResponseDocumentsInternsByUserId(responseCode: Int, data: Seq[ResponseDocumentsInterns])
  implicit val responseAllDocumentsInternsFormat: OFormat[ResponseDocumentsInternsByUserId] = Json.format[ResponseDocumentsInternsByUserId]

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
        documentIntern.origenId,
        documentIntern.destinoId,
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
