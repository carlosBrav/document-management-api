package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{Usuario, UsuarioTable}
import slick.lifted.TableQuery
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[UsuarioTable, Usuario](dbConfigProvider, TableQuery[UsuarioTable])

