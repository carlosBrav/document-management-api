package utils

import pdi.jwt._
import play.api.libs.json._

import scala.util.Try
import utils.Constants.Implicits._

object JWTUtil {

  private val JwtSecretKey = "secretKey"
  private val JwtSecretAlgorithm = JwtAlgorithm.HS256
  val SECONDS_DAY = 86400

  def createToken(payload: Map[String, Any]): String = {
    Jwt.encode(JwtClaim(Json.stringify(Json.toJson(payload))).issuedNow.expiresIn(SECONDS_DAY*60), JwtSecretKey, JwtSecretAlgorithm)
  }

  def decodeToken(token: String): Try[String] = {
    Jwt.decode(token, JwtSecretKey, Seq(JwtSecretAlgorithm))
  }

  def validateToken(token: String): Boolean ={
    Jwt.isValid(token, "secretKey", Seq(JwtSecretAlgorithm))
  }
}
