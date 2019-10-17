package services

import javax.inject.{Inject, Singleton}
import repositories.MovimientosRepository
import models.{Movimientos, MovimientoTable}
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future
import slick.lifted.TableQuery
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}

@Singleton
class MovimientoService @Inject()(
                                 override val repository: MovimientosRepository
                                 )
  extends BaseEntityService[MovimientoTable, Movimientos, MovimientosRepository] {

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
}
