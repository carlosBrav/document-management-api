package services

import javax.inject.Inject
import play.api.Logger
import repositories.{ViewsRepository, MovimientosRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}
import utils.ResponseCodes
import utils.Constants._
import java.util.Calendar


class ViewService @Inject()(
                           repository: ViewsRepository,
                           movimientos: MovimientosRepository
                           ){

  val logger = Logger(this.getClass)


  def getAllView2(day: String) = {
    val timeStampStart = new java.sql.Timestamp(convertToDate(day+" 00:00:00").getTime)
    val timeStampEnd = new java.sql.Timestamp(convertToDate(day+" 23:59:59").getTime)
    val listReturn = {
      for{
        movements <- movimientos.getMovimientos(timeStampStart,timeStampEnd)
        view2Result <- repository.getAllView2Today(timeStampStart,timeStampEnd, movements)
      } yield {
        var movementsTramMov = movements.map(mov => (mov.numTram, mov.movimiento))
        val movementsFilter = view2Result.filter(x => !movementsTramMov.contains((Option(x.tramNum), Option(x.moviNum))))
        Try(movementsFilter)
      }
    }recover{
      case e: Exception => Failure(new Exception(s"${ResponseCodes.GENERIC_ERROR}", e))
    }
    listReturn
  }
}
