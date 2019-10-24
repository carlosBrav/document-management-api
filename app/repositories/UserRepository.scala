package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{Usuario, UsuarioTable}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.{Rol, Dependencias}
import repositories.{RolRepository, DependencyRepository}
import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                               rolRepository: RolRepository,
                               dependencyRepository: DependencyRepository)
  extends BaseEntityRepository[UsuarioTable, Usuario](dbConfigProvider, TableQuery[UsuarioTable]){

  def loadById(id: String) = {
    filter(_.id === id).map(users => Some(users.head)) recover { case _: Exception => None }
  }

  def loadByUserName(userName: String) = {
    val userQuery = query
    val rolQuery = rolRepository.query
    val dependencyQuery = dependencyRepository.query

    val joinUserRol = for {
      ((user, rol), typeDocument) <- userQuery.filter(_.usuario === userName) joinLeft rolQuery on (_.rolId === _.id) joinLeft dependencyQuery on (_._1.dependenciaId === _.id)
    } yield ((user, rol), typeDocument)

    db.run(joinUserRol.result.headOption)
  }

  def getOfficeBoss = {
    filter(_.isOfficeBoss === true).map(users => Some(users.head)) recover { case _: Exception => None}
  }
}

