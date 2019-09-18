package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.jdbc.MySQLProfile.api._
import models.{Vista2, Vista1, Vista2Table, Vista1Table}
import java.util.{Calendar}

import scala.concurrent.Future

@Singleton
class ViewsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider){

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db: JdbcBackend#DatabaseDef = dbConfig.db

  def getAllView2Today(day: String): Future[Seq[Vista2]] = {
    val query = TableQuery[Vista2Table]
    val action = query.sortBy(x => x.moviFecEnv.desc).result
    db.run(action)
  }
}
