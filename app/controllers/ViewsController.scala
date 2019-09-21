package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import services.ViewService
import utils.Constants._
import utils.Constants.Implicits._
import utils._
import helpers.ViewsControllerHelper._
import play.api.Logger


class ViewsController @Inject()(
                               viewService: ViewService,
                               cc: ControllerComponents
                               )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)

  def loadView2: Action[AnyContent] = Action.async{ implicit request =>

    val listResult = for {
      Success(view2) <-viewService.getAllView2("2019-09-17")
    } yield {
      JsonOk(ResponseListView2(ResponseCodes.SUCCESS, "success", view2.map(view => toResponseView2(view))))
    }
    listResult recover {
      case e =>
        val messageError = Constants.get(e.getMessage.toInt)
        JsonOk(
          ResponseError[String](e.getMessage.toInt, s"${messageError.message}")
        )
    }
  }
}
