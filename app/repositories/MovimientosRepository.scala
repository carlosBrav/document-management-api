package repositories

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{MovimientoTable, Movimientos, Dependencias, DependenciaTable}
import slick.jdbc.MySQLProfile.api._
import utils.Constants._


@Singleton
class MovimientosRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)
    extends BaseEntityRepository[MovimientoTable,Movimientos](dbConfigProvider, TableQuery[MovimientoTable]) {

  def getMovementsByDocumentId(documentId: String) ={
    filter(x => x.documentosInternosId === documentId)
  }

  def getMovimientos = {

    filter(x => x.movimiento =!= 0 && x.numTram =!= "" && x.documentosInternosId === "")
  }

  def getMovementsByOffice(officeId: String) = {

    val queryMovements = TableQuery[MovimientoTable]
    val queryDependency = TableQuery[DependenciaTable]

      val joinMovementsDependencies = for {
        ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.dependenciasId1 === officeId) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
      } yield (movement, dependencyOrigin, dependencyDestiny)

      db.run(joinMovementsDependencies.result)
  }

  def getMovementsByTramNum(numTram: String) = {

    val queryMovements = TableQuery[MovimientoTable]
    val queryDependency = TableQuery[DependenciaTable]

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.numTram === numTram) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)

  }

  def getMovementByCurrentDate = {

    val queryMovements = TableQuery[MovimientoTable]
    val queryDependency = TableQuery[DependenciaTable]

    val startStringDate = "2019-09-17 00:00:00"
    val startEndDate = "2019-09-17 23:59:59"
    val startDate = new java.sql.Timestamp(convertToDate(startStringDate).getTime)
    val endDate = new java.sql.Timestamp(convertToDate(startEndDate).getTime)

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.fechaEnvio.between(startDate,endDate)) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)
  }


  def updateFechaIng(documentsIds: Seq[String], userId: String, currentDate: String, asignadoA: String) = {
    db.run(query.filter(x => x.id.inSet(documentsIds))
      .map( x => (x.fechaIngreso, x.fechaModificacion, x.usuarioId, x.asignadoA))
      .update((new java.sql.Timestamp(convertToDate(currentDate).getTime), new java.sql.Timestamp(new Date().getTime), userId, asignadoA)))
  }

  def deleteMovements(movementsIds: Seq[String]) = {
    db.run(query.filter(x => x.id.inSet(movementsIds)).delete)
  }

}
