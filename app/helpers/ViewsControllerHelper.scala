package helpers

import java.sql.Timestamp


import play.api.libs.json.{Format, Json, OFormat, OWrites, Reads, __}
import models.{Movimientos, Vista1, Vista2}
import repositories.MovimientosRepository
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

  case class MovimientoRequest(numTram: Option[String],
                               movimiento: Option[Int],
                               usuarioId: Option[String],
                               observacion: Option[String],
                               estadoDocumento: Option[String],
                               documentoInternoId: Option[String],
                               dependenciaId1: Option[String],
                               dependenciaId2: Option[String],
                               asignadoA: Option[String],
                               indiCod: Option[String],
                               indiNombre: Option[String],
                               fechaEnvio: Option[String],
                               fechaIngreso: Option[String])

  implicit val movimientoRequestFormat: OFormat[MovimientoRequest] = Json.format[MovimientoRequest]

  implicit val responseView2Format: OFormat[ResponseView2] = Json.format[ResponseView2]

  case class ResponseListView2(responseCode: Int, responseMessage: String, data: Seq[ResponseView2])
  implicit val responseListView2Format: OFormat[ResponseListView2] = Json.format[ResponseListView2]

  def toResponseView2(view2: Vista2, vista1: Option[Vista1]) = {

    val vista1Value = vista1.getOrElse(Vista1("",None,"","",Option(""),Option(""),"","",Option(""),
      Option(""),Option(""),"",""))

    ResponseView2(view2.tramNum, view2.moviNum, view2.moviOrigen,view2.depeCod,view2.moviDestino,view2.destCod,
      Option(convertToString(view2.moviFecEnv.get)), Option(""),view2.indiNombre,
    view2.indiCod,view2.moviObs,view2.estaNombre, vista1Value.docuNombre, vista1Value.docuNum, vista1Value.docuSiglas,
      vista1Value.docuAnio)
  }

  case class RequestInsertFromView2(movimientos: Seq[MovimientoRequest], userId: String) {

    def toMovimientosModels: Seq[Movimientos] = {

      val movementModel = movimientos.map(movElement => {
        val movementId = UniqueId.generateId
        val elementModel = Movimientos(Some(movementId),movElement.movimiento,movElement.numTram,movElement.estadoDocumento.get,true,Some(""),movElement.dependenciaId1.get,
          movElement.dependenciaId2.get,Some(""),userId,None,None,Some(new java.sql.Timestamp(convertToDate(movElement.fechaEnvio.get).getTime)),movElement.observacion,movElement.indiNombre,movElement.indiCod,
          Some(new java.sql.Timestamp(new Date().getTime)),Some(new java.sql.Timestamp(new Date().getTime)))

        elementModel
      })
      movementModel
    }
  }

  implicit val requestInsertFromView2Format: OFormat[RequestInsertFromView2] = Json.format[RequestInsertFromView2]

}
