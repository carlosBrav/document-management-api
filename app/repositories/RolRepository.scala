package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import slick.lifted.TableQuery
import models.{Rol, RolTable}


@Singleton
class RolRepository  @Inject()(dbConfigProvider: DatabaseConfigProvider)
extends BaseEntityRepository [RolTable, Rol](dbConfigProvider, TableQuery[RolTable]){

}
