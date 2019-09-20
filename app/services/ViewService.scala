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
    val listReturn = {
      for{
        movements <- movimientos.getMovimientos
        //movementsColumns <- movements.map(mov => (mov.numTram, mov.movimiento))
        view2Result <- repository.getAllView2Today(day, movements)
      } yield {
        val movementsTramMov = movements.map(mov => (mov.numTram, mov.movimiento))
        val movementsFilter = view2Result
        Try(view2Result)
      }
    }recover{
      case e: Exception => Failure(new Exception(s"${ResponseCodes.GENERIC_ERROR}", e))
    }
    listReturn
  }
}
