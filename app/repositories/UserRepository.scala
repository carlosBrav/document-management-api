package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{Usuario, UsuarioTable}
import slick.lifted.TableQuery

@Singleton
class UserRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[UsuarioTable, Usuario](dbConfigProvider, TableQuery[UsuarioTable])

