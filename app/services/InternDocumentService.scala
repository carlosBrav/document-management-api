package services

import javax.inject.{Inject, Singleton}
import repositories._
import models.{DocumentoInternoTable, DocumentosInternos, Movimientos}
import slick.jdbc.MySQLProfile.api._

import scala.util.Failure
import scala.concurrent.Future
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class InternDocumentService @Inject()(
                                 override val repository: DocumentsInternRepository,
                                 movementsRepository: MovimientosRepository,
                                 dependencyRepository: DependencyRepository,
                                 userRepository: UserRepository,
                                 typeDocumentRepository: TypeDocumentRepository
                               )
  extends BaseEntityService[DocumentoInternoTable, DocumentosInternos, DocumentsInternRepository]
{
  def getMaxCorrelative(officeId: String, tipoDocuId: String) = {
    repository.getMaxCorrelative(officeId, tipoDocuId)
  }

  def generateResponseToMovement(newDocumentIntern: DocumentosInternos, newMovement: Movimientos ): Future[Try[Int]] = {
    repository.db.run(
      (repository.saveQuery(newDocumentIntern) andThen movementsRepository.saveQuery(newMovement))
        .transactionally.asTry
    )
  }

  def loadById(documentId: String)  = {
    repository.loadByDocumentId(documentId)
      .map(Try(_))
      .recover {
        case e: Exception => Failure(new Exception("Documento no encontrado"))
      }
  }

  def createCirculars(documentIntern: DocumentosInternos, movements: Seq[Movimientos]): Future[Try[Option[Int]]] = {
    repository.db.run(
      (repository.saveQuery(documentIntern) andThen movementsRepository.saveListQuery(movements)).transactionally.asTry
    )
  }

  def getInternDocuments(userId: String) = {

    val typeDocumentQuery = typeDocumentRepository.query
    val dependencyQuery = dependencyRepository.query
    val userQuery = userRepository.query
    val movementQuery = movementsRepository.query
    val documentQuery = repository.query

    val joinResult = for {
      ((((document, typeDocument), dependency), user),movement)
        <- documentQuery
        .sortBy(_.fechaCreacion.desc)
        .filter(x => x.userId === userId && x.active === true) joinLeft typeDocumentQuery on (_.tipoDocuId === _.id) joinLeft dependencyQuery on (_._1.dependenciaId === _.id) joinLeft userQuery on (_._1._1.userId === _.id) joinLeft movementQuery on (_._1._1._1.id === _.documentosInternosId)
    } yield(document, typeDocument, dependency, user, movement)

    repository.db.run(joinResult.result)
  }

  def deleteDocuments(documentsIds: Seq[String]) = {
    repository.deleteDocuments(documentsIds)
  }

}
