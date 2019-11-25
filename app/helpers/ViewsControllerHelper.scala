package helpers

import play.api.libs.json.{Json, OFormat}
import models.{Movimientos, Vista1, Vista2}
import utils.Constants._
import utils.UniqueId
import java.util.Date

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
                            estaNombre: String,
                           docuNombre: Option[String],
                           docuNum: Option[String],
                           docuSiglas: String,
                           docuAnio: String
                           )

  case class MovimientoRequest(tramNum: Option[String],
                               moviNum: Option[Int],
                               usuarioId: Option[String],
                               moviObs: Option[String],
                               estaNombre: Option[String],
                               documentoInternoId: Option[String],
                               depeCod: Option[String],
                               destCod: Option[String],
                               asignadoA: Option[String],
                               indiCod: Option[String],
                               indiNombre: Option[String],
                               docuNombre: Option[String],
                               docuNum: Option[String],
                               docuSiglas: Option[String],
                               docuAnio: Option[String],
                               previousMovementId: Option[String],
                               moviFecEnv: Option[String],
                               moviFecIng: Option[String])

  implicit val movimientoRequestFormat: OFormat[MovimientoRequest] = Json.format[MovimientoRequest]

  implicit val responseView2Format: OFormat[ResponseView2] = Json.format[ResponseView2]

  case class ResponseListView2(responseCode: Int, responseMessage: String, data: Seq[ResponseView2])
  implicit val responseListView2Format: OFormat[ResponseListView2] = Json.format[ResponseListView2]

  def toResponseView2(view2: Vista2, vista1: Option[Vista1]) = {

    val vista1Value = vista1.getOrElse(Vista1("",None,"","",Option(""),Option(""),"","",Option(""),
      Option(""),Option(""),"",""))

    ResponseView2(view2.tramNum, view2.moviNum, view2.moviOrigen,view2.depeCod,view2.moviDestino,view2.destCod,
      Option(convertToString(view2.moviFecEnv)), Option(""),view2.indiNombre,
    view2.indiCod,view2.moviObs,view2.estaNombre, vista1Value.docuNombre, vista1Value.docuNum, vista1Value.docuSiglas,
      vista1Value.docuAnio)
  }

  case class RequestInsertFromView2(movements: Seq[MovimientoRequest]) {

    def toMovimientosModels(userId: String): Seq[Movimientos] = {

      val movementModel = movements.map(movElement => {
        val movementId = UniqueId.generateId
        val elementModel = Movimientos(Some(movementId),movElement.moviNum,movElement.tramNum,movElement.estaNombre.get,Some(""),
          movElement.depeCod.get,
          movElement.destCod.get,Some(""),
          userId,None,
          Some(new java.sql.Timestamp(convertToDate(movElement.moviFecEnv.get).getTime)),
          movElement.moviObs,
          movElement.indiNombre,
          movElement.indiCod,
          movElement.docuNombre,movElement.docuNum,movElement.docuSiglas, movElement.docuAnio,movElement.previousMovementId,
          Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))

        elementModel
      })
      movementModel
    }
  }

  implicit val requestInsertFromView2Format: OFormat[RequestInsertFromView2] = Json.format[RequestInsertFromView2]

}
