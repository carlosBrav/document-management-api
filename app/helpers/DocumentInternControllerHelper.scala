package helpers

import play.api.libs.json.{Json, OFormat}
import models.{Dependencias, DocumentosInternos, Movimientos, TipoDocumento, Usuario}
import java.util._

import helpers.MovementsControllerHelper.{RequestModelMovements, ResponseModelMovements}
import utils.Constants._
import utils.{Format, UniqueId}

object DocumentInternControllerHelper {

  case class RequestDeleteDocuments(documentsIds: Seq[String])
  implicit val requestDeleteDocumentsFormat: OFormat[RequestDeleteDocuments] = Json.format[RequestDeleteDocuments]

  case class RequestEditCircularDocument(asunto: Option[String],
                                         origenId: Option[String])
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
                                        responsableArea: Option[String],
                                        active: Boolean,
                                        currentDate: Option[String])
  implicit val requestResponseModelDocIntFormat: OFormat[RequestResponseModelDocInt] =
    Json.format[RequestResponseModelDocInt]

  case class RequestModelInternDocuments(
                                             tipoDocuId: String,
                                             numDocumento: Option[Int],
                                             siglas: Option[String],
                                             anio: Option[String],
                                             asunto: Option[String],
                                             origenId: String,
                                             destinoId: Option[String],
                                             userId: Option[String],
                                             firma: Option[String],
                                             responsableArea: Option[String],
                                             currentDate: Option[String]
                                        )

  implicit val requestInternDocumentsFormat: OFormat[RequestModelInternDocuments] =
    Json.format[RequestModelInternDocuments]

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

  case class ResponseCircularDocument(
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
                                       active: Boolean,
                                       userId: Option[String],
                                       userName: Option[String],
                                       userLastName: Option[String],
                                       firma: Option[String],
                                       fechaCreacion: Option[String]
                                     )
  implicit val responseCircularDocument: OFormat[ResponseCircularDocument] = Json.format[ResponseCircularDocument]

  def toResponseCircularDocument(documents: Option[DocumentosInternos],
                                 typeDocument: Option[TipoDocumento],
                                 dependency: Option[Dependencias],
                                 user: Option[Usuario]) : ResponseCircularDocument = {

    val document = documents.getOrElse(DocumentosInternos(Some(""),
      Some(""), "",Some(-1),Some(""),Some(Calendar.getInstance().get(Calendar.YEAR).toString),
      Some(""),Some(""),"",Some(""),true,Some(""),Some(""),Some(""),None,None))

    val response = ResponseCircularDocument(document.id,document.estadoDocumento,document.tipoDocuId,Some(typeDocument.get.nombreTipo),Some("%05d".format(document.numDocumento.get)),
      document.siglas,document.anio, document.asunto,document.observacion,dependency.get.id.get,Some(dependency.get.nombre),document.active,document.userId,Some(user.get.nombre), Some(user.get.apellido), document.firma,
      Some(convertToString(document.fechaCreacion))
    )
    response
  }

  case class ResponseCircularDocumentsByUserId(responseCode: Int, data: Seq[ResponseCircularDocument])
  implicit val responseCircularDocumentsByUserId: OFormat[ResponseCircularDocumentsByUserId] = Json.format[ResponseCircularDocumentsByUserId]

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
      Some(""),Some(""),"",Some(""),true,Some(""),Some(""),Some(""),None,None))

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

  case class RequestCreateCircular(documentIntern: RequestModelInternDocuments,
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
        Some(""),
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

  implicit val requestCreateCircular: OFormat[RequestCreateCircular] = Json.format[RequestCreateCircular]

  case class RequestCreateInternDocument(internDocument: RequestModelInternDocuments){

    def toInternDocument = {
      val internDocumentId = UniqueId.generateId
      DocumentosInternos(Some(internDocumentId),Some("GENERADO"),internDocument.tipoDocuId,internDocument.numDocumento,internDocument.siglas,internDocument.anio,internDocument.asunto,
        Some(""),internDocument.origenId,internDocument.destinoId,true,internDocument.userId,Some(""),internDocument.responsableArea,Some(new java.sql.Timestamp(convertToDate(internDocument.currentDate.get, Format.LOCAL_DATE).getTime)),
        Some(new java.sql.Timestamp(convertToDate(internDocument.currentDate.get, Format.LOCAL_DATE).getTime)))
    }
  }
  implicit val requestCreateInternDocument: OFormat[RequestCreateInternDocument] = Json.format[RequestCreateInternDocument]
}
