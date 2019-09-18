package services

import javax.inject.Inject
import play.api.Logger
import repositories.ViewsRepository

class ViewService @Inject()(
                           repository: ViewsRepository
                           ){

  val logger = Logger(this.getClass)

  def getAllView2(day: String) = {
    val list = repository.getAllView2Today(day)
    list
  }
}
