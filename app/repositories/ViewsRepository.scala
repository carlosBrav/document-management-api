package repositories


import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.basic.DatabaseConfig
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.jdbc.MySQLProfile.api._
import models.{Vista1Table, Vista2Table, Vista2, Vista1}
import models.Movimientos
import scala.concurrent.Future

@Singleton
class ViewsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, movimientoRepository: MovimientosRepository){

  val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]
  val db: JdbcBackend#DatabaseDef = dbConfig.db

  def getAllView2Today(documents: Seq[Movimientos]) : Future[Seq[(Vista2, Option[Vista1])]] = {

    val destinations = List("1001868","1001869","1001870","1001871","1001872")

    val queryVista2 = TableQuery[Vista2Table]
    val queryVista1 = TableQuery[Vista1Table]

    val joinVistas = for {
      (vista2, vista1) <- queryVista2
        .filter(x => x.destCod.inSet(destinations) && x.destCod.inSetBind(destinations))
        .sortBy(x => x.moviFecEnv.desc) joinLeft queryVista1.filter(x => x.docuPric === "1") on (_.tramNum === _.tramNum)
    } yield(vista2, vista1)

    db.run(joinVistas.result)
  }

}
