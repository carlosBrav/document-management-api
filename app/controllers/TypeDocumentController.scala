package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.{TypeDocumentRepository}
import scala.concurrent.ExecutionContext
import utils.Constants._
import utils.Constants.Implicits._
import utils.{Constants, ResponseCodes}
import helpers.TypeDocumentControllerHelper._

class TypeDocumentController @Inject()(
                                        typeDocumentRepository: TypeDocumentRepository,
                                         cc: ControllerComponents
                                       )extends AbstractController(cc) {

  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)


  def getTypeDocuments : Action[AnyContent] = Action.async { implicit request =>
    typeDocumentRepository
      .getAll
      .map(typeDocuments =>
      JsonOk(ResponseTypeDocuments(ResponseCodes.SUCCESS,"Success",typeDocuments.map(typeDocument => toModelTypeDocuments(typeDocument))))
      )
      .recover {
        case e =>
          logger.error("error loading all type documents: " + e.getMessage)
          JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error al intentar mostrar los tipos de documentos"))
      }
  }
}
