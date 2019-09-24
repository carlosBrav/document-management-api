package repositories

import java.sql.Timestamp
import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{MovimientoTable, Movimientos}
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery


@Singleton
class MovimientosRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)
    extends BaseEntityRepository[MovimientoTable,Movimientos](dbConfigProvider, TableQuery[MovimientoTable]) {

  def getMovimientos = {

    filter(x => x.movimiento =!= 0 && x.numTram =!= "" && x.documentosInternosId === "")
  }

  def getMovimientosByOfficeId(officeId: String) = {
    filter(x => x.dependenciasId1 === officeId)
  }

  def updateFechaIng(documentsIds: Seq[String], userId: String) = {
    db.run(query.filter(x => x.id.inSet(documentsIds))
      .map( x => (x.fechaIngreso, x.fechaModificacion, x.usuarioId))
      .update((new java.sql.Timestamp(new Date().getTime), new java.sql.Timestamp(new Date().getTime), userId)))
}
}
