package services

import javax.inject.{Inject, Singleton}
import repositories.{MovimientosRepository, DependencyRepository}
import models.{Movimientos, MovimientoTable}
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

@Singleton
class MovimientoService @Inject()(
                                 override val repository: MovimientosRepository,
                                 dependencyRepository: DependencyRepository
                                 )
  extends BaseEntityService[MovimientoTable, Movimientos, MovimientosRepository] {

  def loadUserMovementsByOfficeId(officeId: String) = {
    val queryMovements = repository.query
    val queryDependency = dependencyRepository.query

    val emptyValue = List("")

    val joinMovements = for{
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.dependenciasId1 === officeId && x.movimiento > 0 && x.fechaIngreso.asColumnOf[Option[java.sql.Timestamp]].isEmpty) joinLeft
        queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    repository.db.run(joinMovements.result)
  }

  def saveMovements(movimientos: Seq[Movimientos]) ={
    repository.db.run(repository.saveListQuery(movimientos).transactionally.asTry)
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

  def updateFechaIngMovements(userId: String, idsMovements: Seq[String], currentDate: String, asignadoA: String): Future[Int] = {
    repository.updateFechaIng(idsMovements, userId, currentDate, asignadoA)
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
}
