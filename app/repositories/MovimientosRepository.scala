package repositories

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{MovimientoTable, Movimientos, Dependencias, DependenciaTable}
import slick.jdbc.MySQLProfile.api._
import utils.Constants._
import utils.STATUS
import utils.Format


@Singleton
class MovimientosRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider, dependencyRepository: DependencyRepository)
    extends BaseEntityRepository[MovimientoTable,Movimientos](dbConfigProvider, TableQuery[MovimientoTable]) {

  val queryMovements = TableQuery[MovimientoTable]
  val queryDependency = TableQuery[DependenciaTable]

  def loadByInternDocumentId(internDocumentIds: Seq[String]) ={
    db.run(query.filter(x=>x.documentosInternosId.inSet(internDocumentIds)).result.asTry)
  }

  def getMovimientos = {
    filter(x => x.movimiento =!= 0 && x.numTram =!= "" && x.documentosInternosId === "")
  }

  def getMovementsByOffice(officeId: String) = {

      val joinMovementsDependencies = for {
        ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.dependenciasId1 === officeId) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
      } yield (movement, dependencyOrigin, dependencyDestiny)

      db.run(joinMovementsDependencies.result)
  }

  def getMovementsByTramNum(numTram: String) = {

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.numTram === numTram) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)

  }

  def getMovementByCurrentDate = {

    val startStringDate = "2019-09-17 00:00:00"
    val startEndDate = "2019-09-17 23:59:59"
    val startDate = new java.sql.Timestamp(convertToDate(startStringDate).getTime)
    val endDate = new java.sql.Timestamp(convertToDate(startEndDate).getTime)

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.fechaEnvio.between(startDate,endDate) && x.fechaIngreso.asColumnOf[Option[java.sql.Timestamp]].isEmpty && x.dependenciasId === "100392" && x.estadoDocumento === STATUS.IN_PROCESS) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)
  }

  def getMovementByAssignedTo(userId: String) = {

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.asignadoA === userId && x.estadoDocumento === STATUS.IN_PROCESS) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)
  }


  def updateFechaIng(documentsIds: Seq[String], userId: String, currentDate: String, asignadoA: String) = {
    db.run(query.filter(x => x.id.inSet(documentsIds))
      .map( x => (x.fechaIngreso, x.fechaModificacion, x.usuarioId, x.asignadoA))
      .update((new java.sql.Timestamp(convertToDate(currentDate, Format.LOCAL_DATE).getTime), new java.sql.Timestamp(new Date().getTime), userId, asignadoA)))
  }

  def updateStatusMovement(movementId: String) = {
    db.run(query.filter(x=> x.id === movementId).map(x=>x.estadoDocumento).update("DERIVADO"))
  }

  def updateStatusMovementAdmin(movementId: String) = {
    db.run(query.filter(x=> x.id === movementId).map(x=>(x.estadoDocumento, x.fechaIngreso, x.fechaModificacion)).update(("DERIVADO", new java.sql.Timestamp(new Date().getTime),new java.sql.Timestamp(new Date().getTime))))
  }

  def deleteMovements(movementsIds: Seq[String]) = {
    db.run(query.filter(x => x.id.inSet(movementsIds)).delete)
  }

  def saveDerivedAssignedDocuments(userId: String, oldMovements: Seq[String], newMovements: Seq[Movimientos]) = {
    db.run(
      (query.filter(x=> x.id inSet Traversable(oldMovements.reduce(_ ++ _))).map(x=>x.estadoDocumento)
        .update("DERIVADO") andThen saveListQuery(newMovements)).transactionally.asTry
    )
  }

  def saveDerivedDocuments(userId: String, oldMovements: Seq[String], newMovements: Seq[Movimientos]) = {
    db.run(
      (query.filter(x=> x.id inSet Traversable(oldMovements.reduce(_ ++ _))).map(x=>(x.fechaIngreso,x.asignadoA, x.estadoDocumento))
        .update(new java.sql.Timestamp(new Date().getTime), userId, "DERIVADO") andThen saveListQuery(newMovements)).transactionally.asTry
    )
  }

  def loadMovementsToAnalyze() = {
    val internOffices = List("1001868","1001869","1001870","1001871","1001872")

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.filter(x => x.dependenciasId1.inSet(internOffices) && x.estadoDocumento === STATUS.IN_PROCESS) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)
  }

  def loadAdvancedSearch(numTram: Option[String], observation: Option[String], officeId: Option[String]) = {

    val joinMovementsDependencies = for {
      ((movement, dependencyOrigin), dependencyDestiny) <- queryMovements.
        filter(x=>x.numTram =!= x.id).
        filter(x=>x.observacion.like(s"%${observation.getOrElse("")}%")).
        filterOpt(numTram)(_.numTram === _).
          filterOpt(officeId)(_.dependenciasId1 === _) joinLeft queryDependency on (_.dependenciasId === _.id) joinLeft queryDependency on (_._1.dependenciasId1 === _.id)
    } yield (movement, dependencyOrigin, dependencyDestiny)

    db.run(joinMovementsDependencies.result)
  }
}
