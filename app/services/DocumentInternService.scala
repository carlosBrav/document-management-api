package services

import javax.inject.{Inject, Singleton}
import repositories.{DocumentsInternRepository, MovimientosRepository}
import models.{DocumentoInternoTable, DocumentosInternos, Movimientos}
import slick.jdbc.MySQLProfile.api._
import scala.util.Failure
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

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

  def getDocumentsInternsByTipoDocuId(tipoDocuId: String) : Future[Seq[DocumentosInternos]] = {
    val documents = repository.filter(x => x.tipoDocuId === tipoDocuId)
    documents
  }

  def createCiculares(userId: String, officeId: String, documentIntern: DocumentosInternos, movements: Seq[Movimientos]): Future[Try[Option[Int]]] = {
    repository.db.run(
      (repository.saveQuery(documentIntern) andThen movementsRepository.saveListQuery(movements)).transactionally.asTry
    )
  }

}
