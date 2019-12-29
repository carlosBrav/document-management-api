package helpers
import helpers.HomeControllerHelper.ResponseDependency
import play.api.libs.json.{JsObject, Json, OFormat}

object OfficesControllerHelper {

  case class ListOfficesResponse(responseCode: Int, data: Seq[ResponseDependency])
  implicit val listOfficesResponseFormat: OFormat[ListOfficesResponse] = Json.format[ListOfficesResponse]
}
