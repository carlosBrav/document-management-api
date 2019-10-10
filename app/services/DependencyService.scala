package services

import javax.inject.{Inject, Singleton}
import repositories.DependencyRepository
import models.{DependenciaTable, Dependencias}

import scala.concurrent.Future

@Singleton
class DependencyService @Inject()(
                                   override val repository: DependencyRepository
                                 )
  extends BaseEntityService[DependenciaTable,Dependencias,DependencyRepository]{

  def getAllDependencies: Future[Seq[Dependencias]] = {
    repository.getAll
  }
}
