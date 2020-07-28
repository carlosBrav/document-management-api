package services

import javax.inject.{Inject, Singleton}
import models.{Usuario, UsuarioTable}
import repositories.{DocumentsInternRepository, MovimientosRepository, UserRepository, RolRepository, DependencyRepository}
import utils.ResponseCodes
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Try}
import utils.BCryptPass.validateHashPass
import play.api.Logger

@Singleton
class UserService @Inject()(
                             override val repository: UserRepository,
                             documentInternsRepository: DocumentsInternRepository,
                             rolRepository: RolRepository,
                             dependencyRepository: DependencyRepository,
                             movimientosRepository: MovimientosRepository
                           )
  extends BaseEntityService[UsuarioTable, Usuario, UserRepository]{

  val logger = Logger(this.getClass)

  def processLogin(usuario: String, password: String) = {
    println("PASS ", password)
    val userLogin = {
      for {
        userResult <- repository.loadByUserName(usuario)
      } yield {
        if(userResult.isDefined){
          val user = userResult.get._1._1
          if(user.estado){
            if (validateHashPass(password, user.password)) {
              Try(userResult.get)
            }
            else
              Failure(new Exception(s"${ResponseCodes.UNAUTHORIZED}"))
          }else{
            Failure(new Exception(s"${ResponseCodes.INACTIVE_USER}"))
          }
        }else{
          Failure(new Exception(s"${ResponseCodes.USER_NOT_FOUND}"))
        }
      }
    } recover{
      case e: Exception =>
        logger.error(s"Error login: $e")
        Failure(new Exception(s"${ResponseCodes.GENERIC_ERROR}"))
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

  def getOfficeBoss = {
    repository.getOfficeBoss
  }

  def getAllUsers() = {
    val rolQuery = rolRepository.query
    val officeQuery = dependencyRepository.query
    val userQuery = repository.query

    val joinResult = for {
      ((user, rol), office) <- userQuery joinLeft rolQuery on (_.rolId === _.id) joinLeft officeQuery on (_._1.dependenciaId === _.id)
    }yield(user,rol,office)

    repository.db.run(joinResult.result)
  }
}
