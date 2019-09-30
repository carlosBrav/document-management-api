package services

import javax.inject.{Inject, Singleton}
import models.{DocumentosInternos, Movimientos, Usuario, UsuarioTable}
import repositories.{DocumentsInternRepository, MovimientosRepository, UserRepository}
import utils.ResponseCodes
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}
import utils.BCryptPass.validateHashPass
import utils.CustomExceptions
import play.api.Logger

@Singleton
class UserService @Inject()(
                             override val repository: UserRepository,
                             documentInternsRepository: DocumentsInternRepository,
                             movimientosRepository: MovimientosRepository
                           )
  extends BaseEntityService[UsuarioTable, Usuario, UserRepository]{

  val logger = Logger(this.getClass)

  def processLogin(usuario: String, password: String): Future[Try[Usuario]] = {
    val userFilterByUsuario = repository.filter(_.usuario === usuario)

    val userLogin = {
      for {
        userResult <- userFilterByUsuario.map(_.head)
      } yield {
        if(userResult.estado){
          if (validateHashPass(password, userResult.password)) {
            Try(userResult)
          }
          else
            Failure(new Exception(s"${ResponseCodes.UNAUTHORIZED}"))
        }else{
          Failure(new Exception(s"${ResponseCodes.USUARIO_INACTIVO}"))
        }
      }
    } recover{
      case e: Exception =>
        logger.error(s"Error login: $e")
        Failure(new Exception(s"${ResponseCodes.USER_NOT_FOUND}"))
    }
    userLogin
  }

  def loadById(id: String) = {
    val userResult = repository.loadById(id)
    userResult.map {
      case Some(user) => Try(user)
      case None => Failure(new Exception("User not found"))
    }
  }
}
