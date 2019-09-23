package services

import javax.inject.{Inject, Singleton}
import repositories.{MovimientosRepository}
import models.{Movimientos, MovimientoTable}
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class MovimientoService @Inject()(
                                 override val repository: MovimientosRepository
                                 )
  extends BaseEntityService[MovimientoTable, Movimientos, MovimientosRepository]
{
  def saveMovimientosFromView2(movimientos: Seq[Movimientos]) ={
    repository.db.run(repository.saveListQuery(movimientos).transactionally.asTry)
}

}
