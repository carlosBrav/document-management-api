package utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.libs.functional.syntax._

object Constants {

  def JsonOk[T](t: T)(implicit writes: Writes[T]): Result = Ok(Json.toJson(t))

  case class Response[T](responseCode: Int, responseMessage: String, data: T)

  case class ResponseErrorLogin[T](responseCode: Int, responseMessage: String, errors: T)

  case class ResponseError[T](responseCode: Int, responseMessage: T)


  object Implicits {

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
}

object ResponseCodes {
  val SUCCESS = 0
  val GENERIC_ERROR = 100
  val CONFLICT_ERROR = 409
  val MISSING_FIELDS = 422
  val UNAUTHORIZED = 401
  val USER_NOT_FOUND= 404
  val FORBIDDEN  = 403
  val USER_NOT_VALIDATED = 101
  val USER_DISABLED = 102
  val DUPLICATED_INFO = 405
}
