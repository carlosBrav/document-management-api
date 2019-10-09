package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{Usuario, UsuarioTable}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import models.{Rol, RolTable}
import repositories.RolRepository
import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider,
                               rolRepository: RolRepository)
  extends BaseEntityRepository[UsuarioTable, Usuario](dbConfigProvider, TableQuery[UsuarioTable]){

  def loadById(id: String) = {
    filter(_.id === id).map(users => Some(users.head)) recover { case _: Exception => None }
  }

  def loadByUserName(userName: String): Future[Option[(Usuario, Option[Rol])]] = {
    val userQuery = query
    val rolQuery = rolRepository.query

    val joinUserRol = for {
      (user, rol) <- userQuery.filter(_.usuario === userName) joinLeft rolQuery on (_.rolId === _.id)
    } yield (user, rol)

    db.run(joinUserRol.result.headOption)
  }
}

