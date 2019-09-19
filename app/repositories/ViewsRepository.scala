package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.jdbc.MySQLProfile.api._
import models.{Vista1, Vista1Table, Vista2, Vista2Table}

import utils.Constants.convertToDate
import repositories.{MovimientosRepository} 
import models.{Movimientos}

import scala.concurrent.Future

@Singleton
class ViewsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, movimientoRepository: MovimientosRepository){

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db: JdbcBackend#DatabaseDef = dbConfig.db

  def getAllView2Today(day: String, documents: Seq[Movimientos]): Future[Seq[Vista2]] = {
    println(s"documents $documents")
    val dayStart = day+" 00:00:00"
    val dayEnd = day+" 23:59:59"
    val timeStampStart = new java.sql.Timestamp(convertToDate(dayStart).getTime)
    val timeStampEnd = new java.sql.Timestamp(convertToDate(dayEnd).getTime)
    val query = TableQuery[Vista2Table]
    val action = query.filter(x => x.moviFecEnv.between(timeStampStart,timeStampEnd)).sortBy(x => x.moviFecEnv.desc).result
    db.run(action)
  }
}
