package controllers

import java.io.File

import javax.inject._
import play.api._
import play.api.mvc._
import services.{DependencyService, UserService}
import helpers.HomeControllerHelper._
import helpers.UsersControllerHelper._
import play.api.libs.json.Json
import utils.Constants._
import utils.Constants.Implicits._
import utils._
import utils.ResponseCodes

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               dependencyService: DependencyService,
                               userService: UserService) extends AbstractController(cc) {
  implicit val ec: ExecutionContext = defaultExecutionContext
  val logger = Logger(this.getClass)
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(path:String): Action[AnyContent] = Action { implicit request =>
    Ok.sendFile(new File("public/index.html"))
  }

  def initialState: Action[AnyContent] = Action.async { implicit request =>
    val result = for {
      dependencyResponses <- dependencyService.getAllDependencies.map {
        dependencies =>
          dependencies.map {
            dependency => ResponseDependency(dependency.id,
              dependency.nombre,
              dependency.estado,
              dependency.siglas,
              dependency.codigo,
              dependency.tipo.getOrElse("-1"))
          }
      }
      userResponse <- userService.getAll.map {
        userList =>
          userList.map{
            user => ResponseUser(user.id,user.usuario,user.estado,user.rolId,user.nombre,user.apellido,user.telefono,user.dependenciaId,
              user.isSubOfficeBoss,user.isOfficeBoss)
          }
      }
    } yield JsonOk(
      InitialStateResponse(ResponseCodes.SUCCESS,"Success",Json.obj("dependencies" -> dependencyResponses, "users"-> userResponse))
    )
    result recover {
      case ex =>
        logger.error("error initial: " + ex.getMessage)
        JsonOk(ResponseError[String](ResponseCodes.GENERIC_ERROR, "Error"))
    }
  }
  
}
