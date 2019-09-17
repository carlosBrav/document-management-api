package utils

import play.api.libs.json.Reads.verifying
import play.api.libs.functional.syntax._
import play.api.libs.json._

object RequestUtils {

  def passwordCheck: Reads[String] = {
    (__ \ "password").readWithDefault[String]("fieldIsEmptyString").map(_.trim)
      .filter(JsonValidationError("Please provide a password")) {
        x => !x.equals("fieldIsEmptyString")
      }
      .filter(JsonValidationError("passwords must contain a number")) {
        _.exists(_.isDigit)
      }
  }

  def emptyStringFieldCheck(field: String, errorMessage: String): Reads[String] = {
    (__ \ field).readWithDefault[String]("fieldIsEmptyString")
      .filter(JsonValidationError(errorMessage)) {
        x => !x.equals("fieldIsEmptyString") && !x.equals("")
      }
  }

  def emptyStringFieldCheckOption(field: String, errorMessage: String, pattern: String = """^$|^(?!\s*$).+|$"""): Reads[Option[String]] = {
    (__ \ field).readNullableWithDefault[String](Option("")).
      filter(JsonValidationError(errorMessage)) {
        x => {
          x.getOrElse("").matches(pattern) || x.getOrElse("").isEmpty
        }
      }
  }

  def emailCheck: Reads[String] = {
    (__ \ "email").readWithDefault[String]("fieldIsEmptyString").map(_.trim)
      .filter(JsonValidationError("Provide email")) {
        x => !x.equals("fieldIsEmptyString")
      }.filter(JsonValidationError("Invalid email format")) {
      _.matches("""^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""")
    }
  }
}
