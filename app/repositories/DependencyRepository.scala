package repositories

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import models.{DependenciaTable, Dependencias}
import slick.lifted.TableQuery

@Singleton
class DependencyRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends BaseEntityRepository[DependenciaTable,Dependencias](dbConfigProvider, TableQuery[DependenciaTable]){

}
