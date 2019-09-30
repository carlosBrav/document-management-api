package services

import javax.inject.{Inject, Singleton}
import repositories.{DocumentsInternRepository, MovimientosRepository}
import services.DocumentInternService
import models.{DocumentoInternoTable, DocumentosInternos, Movimientos}
import slick.jdbc.MySQLProfile.api._
import scala.util.{Failure, Try}
import scala.concurrent.Future
import scala.util.Try

@Singleton
class DocumentInternService @Inject()(
                                 override val repository: DocumentsInternRepository,
                                 movementsRepository: MovimientosRepository
                               )
  extends BaseEntityService[DocumentoInternoTable, DocumentosInternos, DocumentsInternRepository]
{
  def getMaxCorrelative(officeId: String, tipoDocuId: String): Future[DocumentosInternos] = {
    repository.getMaxCorrelative(officeId, tipoDocuId)
  }

  def generateResponseToMovement(newDocumentIntern: DocumentosInternos, newMovement: Movimientos ): Future[Try[Int]] = {
    repository.db.run(
      (repository.saveQuery(newDocumentIntern) andThen movementsRepository.saveQuery(newMovement))
        .transactionally.asTry
    )
  }

  def loadById(documentId: String) : Future[Try[DocumentosInternos]] = {
    val document = repository.filter(_.id === documentId)
    document.map(_.head)
      .map(Try(_))
      .recover {
        case e: Exception => Failure(new Exception("Documento no encontrado"))
      }
  }

}
