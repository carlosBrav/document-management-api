package services

import javax.inject.Inject
import play.api.Logger
import repositories.{MovimientosRepository, ViewsRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}
import utils.ResponseCodes
import utils.Constants.Implicits._

import scala.concurrent.Future
import models.{Vista1, Vista2}

class ViewService @Inject()(
                           repository: ViewsRepository,
                           movimientos: MovimientosRepository
                           ){

  val logger = Logger(this.getClass)

  def getAllView2 : Future[Try[Seq[(Vista2, Option[Vista1])]]] = {
    val listReturn = {
      for{
        movements <- movimientos.getMovimientos
        joinView <- repository.getAllView2Today(movements)
      } yield {
        val movementsTramMov = movements.map(mov => (mov.numTram, mov.movimiento))
        val joinViewFilter = joinView.filter(x => x._1.moviFecIng.getOrElse("") == "")
          .filter(x => !movementsTramMov.contains((Option(x._1.tramNum), Option(x._1.moviNum)))).sortBy(_._1.moviFecEnv.get)
        Try(joinViewFilter)
      }
    }recover{
      case e: Exception => Failure(new Exception(s"${ResponseCodes.GENERIC_ERROR}", e))
    }
    listReturn
  }

  def getMovementsByTramNum(tramNum: String) = {
    repository.getMovementsByTramNum(tramNum)
  }

}
