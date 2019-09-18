package helpers

import play.api.libs.json.{Format, Json, OFormat, OWrites, Reads, __}
import models.{Vista2}
import utils.Constants._

object ViewsControllerHelper {

  case class ResponseView2(tramNum: String,
                            moviNum: Int,
                            moviOrigen: String,
                            depeCod: String,
                            moviDestino: String,
                            destCod: String,
                            moviFecEnv: Option[String],
                            moviFecIng: Option[String],
                            indiNombre: Option[String],
                            indiCod: Option[String],
                            moviObs: Option[String],
                            estaNombre: String)

  implicit val responseView2Format: OFormat[ResponseView2] = Json.format[ResponseView2]

  case class ResponseListView2(responseCode: Int, responseMessage: String, data: Seq[ResponseView2])
  implicit val responseListView2Format: OFormat[ResponseListView2] = Json.format[ResponseListView2]

  def toResponseView2(view2: Vista2) = {
    ResponseView2(view2.tramNum, view2.moviNum, view2.moviOrigen,view2.depeCod,view2.moviDestino,view2.destCod,
      Option(convertToString(view2.moviFecEnv.get)), Option(""),view2.indiNombre,
    view2.indiCod,view2.moviObs,view2.estaNombre)
  }
}
