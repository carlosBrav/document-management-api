package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.libs.functional.syntax._

object ResponseCodes {
  val SUCCESS = 0
  val GENERIC_ERROR = 100
  val CONFLICT_ERROR = 409
  val MISSING_FIELDS = 422
  val UNAUTHORIZED = 401
  val USER_NOT_FOUND= 404
  val FORBIDDEN  = 403
  val USER_DISABLED = 102
  val DUPLICATED_INFO = 405
  val USUARIO_INACTIVO = 101
  val PASSWORD_NOT_MATCH = 103
}

object Format {
  val DATE = "yyyy-MM-dd HH:mm:ss"
}

object Constants {

  def JsonOk[T](t: T)(implicit writes: Writes[T]): Result = Ok(Json.toJson(t))

  case class Response[T](responseCode: Int, responseMessage: String, data: T)

  case class ResponseErrorLogin[T](responseCode: Int, responseMessage: String, errors: T)

  case class ResponseError[T](responseCode: Int, responseMessage: T)

  case class CustomResponseException(codeException: Int, message: String)

  private val genericError = CustomResponseException(ResponseCodes.GENERIC_ERROR,"Generic Error")

  private val  errorsMap: Map[Int, CustomResponseException] = Map(
    ResponseCodes.SUCCESS -> CustomResponseException(ResponseCodes.SUCCESS, "success"),
    ResponseCodes.MISSING_FIELDS -> CustomResponseException(ResponseCodes.MISSING_FIELDS, "missing required parameter"),
    ResponseCodes.USER_NOT_FOUND -> CustomResponseException(ResponseCodes.USER_NOT_FOUND, "No se ha encontrado al usuario."),
  )

  def get(code: Int): CustomResponseException = errorsMap.getOrElse(code,genericError)

  object Implicits {

    implicit def ordered: Ordering[Timestamp] = new Ordering[Timestamp] {
      def compare(x: Timestamp, y: Timestamp): Int = y compareTo x
    }

    implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
      //      todo: Write correct implementation
      override def reads(json: JsValue): JsResult[Timestamp] = {
        JsSuccess(new Timestamp(System.currentTimeMillis()))
      }

      override def writes(o: Timestamp): JsValue = {
        val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        JsString(simpleDateFormat.format(new Date(o.getTime)))
      }
    }

    implicit def responseFormat[T](implicit fmt: Format[T]): Format[Response[T]] =
      (
        (__ \ "responseCode").format[Int] and
          (__ \ "responseMessage").format[String] and
          (__ \ "data").format[T](fmt)
        ) (Response.apply, unlift(Response.unapply))

    implicit def responseErrorLoginFormat[T](implicit fmt: Format[T]): Format[ResponseErrorLogin[T]] =
      (
        (__ \ "responseCode").format[Int] and
          (__ \ "responseMessage").format[String] and
          (__ \ "errors").format[T](fmt)
        ) (ResponseErrorLogin.apply, unlift(ResponseErrorLogin.unapply))

    implicit def responseErrorFormat[T](implicit fmt: Format[T]): Format[ResponseError[T]] =
      (
        (__ \ "responseCode").format[Int] and
          (__ \ "responseMessage").format[T]
        ) (ResponseError.apply, unlift(ResponseError.unapply))

    //Generic Map  Write
    implicit val mapWrites: Writes[Map[String, Any]] = (o: Map[String, Any]) => {
      JsObject(
        o.map { kvp =>
          kvp._1 -> (
            kvp._2 match {
              case x: String => JsString(x)
              case x: Int => JsNumber(x)
              case _ => JsNull // Do whatever you want here.
            }
            )
        }
      )
    }
  }

  def invalidResponseFormatter(invalidRequest: Seq[(JsPath, Seq[JsonValidationError])]): Seq[String] = {
    for {
      reqMessages <- invalidRequest.map { case reqItem if reqItem._2.nonEmpty => reqItem._2 }
      errorSeq <- reqMessages.map(messageObj => if (messageObj != null && messageObj.message.nonEmpty) messageObj.message else "")
    } yield errorSeq
  }

  def convertToString(ts: Option[Timestamp]):String = {
    val time = ts.getOrElse("")
    if(time == ""){
      ""
    }else{
      val df:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      df.format(time)
    }
  }

  def convertToDate(s: String): Date = {
    if(!s.isEmpty){
      val dateFormat = new SimpleDateFormat(Format.DATE)
      dateFormat.parse(s)
    }else{
      new Date()
    }
  }
}
