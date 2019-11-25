package services

import java.util.Calendar

import javax.inject.{Inject, Singleton}
import repositories.{DependencyRepository, DocumentsInternRepository, MovimientosRepository, TypeDocumentRepository}
import models.{DocumentosInternos, MovimientoTable, Movimientos}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

@Singleton
class MovimientoService @Inject()(
                                 override val repository: MovimientosRepository,
                                 dependencyRepository: DependencyRepository,
                                 internDocumentRepository: DocumentsInternRepository,
                                 typeDocumentRepository: TypeDocumentRepository
                                 )
  extends BaseEntityService[MovimientoTable, Movimientos, MovimientosRepository] {


  def loadUserMovementsByOfficeId(officeId: String) = {
    val queryMovements = repository.query
    val queryDependency = dependencyRepository.query
    val queryInternDocument = internDocumentRepository.query

    val joinMovements = for{
      (((movement, dependencyOrigin), dependencyDestiny),internDocument) <- queryMovements.filter(x => x.dependenciasId1 === officeId && x.movimiento > 0 && x.fechaIngreso.asColumnOf[Option[java.sql.Timestamp]].isEmpty) joinLeft
        queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id) joinLeft queryInternDocument on (_._1._1.documentosInternosId === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny, internDocument)

    repository.db.run(joinMovements.sortBy(_._1.fechaEnvio.desc).result)
  }

  def saveMovements(movements: Seq[Movimientos]) = {
    repository.db.run(repository.saveListQuery(movements).transactionally.asTry)
  }

  def saveDerivedAssignedMovements(userId: String, oldMovements: Seq[String], newMovements: Seq[Movimientos]) ={
    repository.saveDerivedAssignedDocuments(userId,oldMovements,newMovements)
  }

  def saveDerivedMovements(userId: String, oldMovements: Seq[String], newMovements: Seq[Movimientos]) ={
    repository.saveDerivedDocuments(userId,oldMovements,newMovements)
  }

  def loadAdminMovementsByOffice(officeId: String)={
    val queryInternDocument = internDocumentRepository.query
    val queryDependency = dependencyRepository.query
    val queryMovements = repository.query
    val queryTypeDocuments = typeDocumentRepository.query

    val joinInternDocument = for {
      (((internDocument,dependency),typeDocument),movement) <- queryInternDocument.filter(x => x.destinoId === officeId) joinLeft queryDependency on (_.origenId === _.id) joinLeft
      queryTypeDocuments on (_._1.tipoDocuId === _.id) joinLeft queryMovements on (_._1._1.id === _.documentosInternosId)
    } yield(internDocument, dependency, typeDocument, movement)

    repository.db.run(joinInternDocument.sortBy(_._1.fechaCreacion.desc).result)
  }

  def loadMovementsByOffice(officeId: String)= {
    val result = repository.getMovementsByOffice(officeId)
    result
  }

  def loadMovementsByTramNum(numTram: String) = {
    val result = repository.getMovementsByTramNum(numTram)
    result
  }

  def loadMovementsByCurrentDate = {
    val result = repository.getMovementByCurrentDate
    result
  }

  def loadMovementsByAssignedTo(userId: String) = {
    repository.getMovementByAssignedTo(userId)
  }

  def updateFechaIngMovements(userId: String, idsMovements: Seq[String], currentDate: String, asignadoA: String): Future[Int] = {
    repository.updateFechaIng(idsMovements, userId, currentDate, asignadoA)
  }

  def updateStatusToMovement(movementId: String) = {
    repository.updateStatusMovement(movementId)
  }

  def updateStatusToMovementAdmin(movementId: String) = {
    repository.updateStatusMovementAdmin(movementId)
  }

  def deleteMovement(movementsIds: Seq[String]) = {
    val result = repository.deleteMovements(movementsIds)
    result
  }

  def getInternDocumentsByDocumentId(documentId: String) = {

    val queryMovements = repository.query
    val queryDependency = dependencyRepository.query

    val joinMovements = for {
      (movement, dependencyDestiny) <- queryMovements.filter(x => x.documentosInternosId === documentId) joinLeft queryDependency on (_.dependenciasId1 === _.id)
    } yield(movement, dependencyDestiny)

    repository.db.run(joinMovements.result)
  }

  def loadByInternDocumentId(internDocumentId: String) = {
    repository.loadByInternDocumentId(internDocumentId)
  }
}
