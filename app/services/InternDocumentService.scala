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

  def getInternDocumentsAdmin(officeId: String) = {
    val internDocumentQuery = repository.query
    val dependencyQuery = dependencyRepository.query
    val typeDocumentQuery= typeDocumentRepository.query

    val joinResult = for {
      (((document, typeDocument),dependencyOrigin),dependencyDestiny) <- internDocumentQuery.filter(x => x.origenId =!= officeId && x.destinoId === officeId && x.estadoDocumento === "EN PROCESO") joinLeft
        typeDocumentQuery on (_.tipoDocuId === _.id) joinLeft dependencyQuery on (_._1.origenId === _.id) joinLeft dependencyQuery on (_._1._1.destinoId === _.id)
    }yield(document, typeDocument,dependencyOrigin,dependencyDestiny)

    repository.db.run(joinResult.sortBy(_._1.fechaCreacion.desc).result)
  }

  def getInternDocuments(userId: String) = {

    val typeDocumentQuery = typeDocumentRepository.query
    val dependencyQuery = dependencyRepository.query
    val userQuery = userRepository.query
    val movementQuery = movementsRepository.query
    val documentQuery = repository.query

    val joinResult = for {
      (((((document, typeDocument), dependency), dependencyDestiny), user),movement)
        <- documentQuery
        .filter(x => x.userId === userId && !(x.tipoDocuId.inSetBind(List("54545","74545"))) ) joinLeft typeDocumentQuery on (_.tipoDocuId === _.id) joinLeft dependencyQuery on (_._1.origenId === _.id) joinLeft dependencyQuery on (_._1._1.destinoId === _.id) joinLeft userQuery on (_._1._1._1.userId === _.id) joinLeft movementQuery on (_._1._1._1._1.id === _.documentosInternosId)
    } yield(document, typeDocument, dependency, dependencyDestiny, user, movement)

    repository.db.run(joinResult.sortBy(_._1.fechaCreacion.desc).result)
  }

  def getInternDocumentsByOfficeId(typeDocumentId: String, officeId: String) = {

    val typeDocumentQuery = typeDocumentRepository.query
    val dependencyQuery = dependencyRepository.query
    val userQuery = userRepository.query
    val movementQuery = movementsRepository.query
    val documentQuery = repository.query

    val joinResult = for {
      (((((document, typeDocument), dependency), dependencyDestiny), user),movement)
        <- documentQuery
        .filter(x => x.origenId === officeId && x.tipoDocuId===typeDocumentId) joinLeft typeDocumentQuery on (_.tipoDocuId === _.id) joinLeft dependencyQuery on (_._1.origenId === _.id) joinLeft dependencyQuery on (_._1._1.destinoId === _.id) joinLeft userQuery on (_._1._1._1.userId === _.id) joinLeft movementQuery on (_._1._1._1._1.id === _.documentosInternosId)
    } yield(document, typeDocument, dependency, dependencyDestiny, user, movement)

    repository.db.run(joinResult.sortBy(_._1.fechaCreacion.desc).result)
  }

  def getCircularDocuments(userId: String) = {
    val typeDocumentQuery = typeDocumentRepository.query
    val dependencyQuery = dependencyRepository.query
    val userQuery = userRepository.query
    val documentQuery = repository.query

    val joinResult = for {
      (((document, typeDocument), dependency), user)
        <- documentQuery
        .filter(x => x.userId === userId && x.tipoDocuId.inSet(List("54545","74545"))) joinLeft typeDocumentQuery on (_.tipoDocuId === _.id) joinLeft dependencyQuery on (_._1.origenId === _.id) joinLeft userQuery on (_._1._1.userId === _.id)
    } yield(document, typeDocument, dependency, user)

    repository.db.run(joinResult.sortBy(_._1.fechaCreacion.desc).result)
  }

  def deleteDocuments(documentsIds: Seq[String], previousMovementId: Seq[String]) = {
    repository.deleteDocuments(documentsIds,previousMovementId)
  }

}
