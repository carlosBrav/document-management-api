package repositories

import java.sql.Timestamp

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
}
