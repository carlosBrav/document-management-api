package utils


object ErrorsResponse {
  case class CustomException(codeException: Int, message: String)

  private val genericError = CustomException(ResponseCodes.GENERIC_ERROR,"Generic Error")

  private val  errorsMap: Map[Int, CustomException] = Map(
    ResponseCodes.SUCCESS -> CustomException(ResponseCodes.SUCCESS, "success"),
    ResponseCodes.MISSING_FIELDS -> CustomException(ResponseCodes.MISSING_FIELDS, "missing required parameter"),
    ResponseCodes.UNAUTHORIZED -> CustomException(ResponseCodes.UNAUTHORIZED, "unauthorized"),
    ResponseCodes.USER_NOT_FOUND -> CustomException(ResponseCodes.USER_NOT_FOUND, "A user was not found with that email, Please create an account"),
    ResponseCodes.USER_NOT_VALIDATED-> CustomException(ResponseCodes.USER_NOT_VALIDATED, "Account not verified. Please verify yor account")
  )

  def get(code: Int): CustomException = errorsMap.getOrElse(code,genericError)
}
