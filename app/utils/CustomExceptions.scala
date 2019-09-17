package utils

object CustomExceptions {

  case class CustomException(codeException: Int, message: String) extends Exception

  private val genericError = CustomException(ResponseCodes.GENERIC_ERROR,"Generic Error")

  private val  errorsMap: Map[Int, CustomException] = Map(
    ResponseCodes.MISSING_FIELDS -> CustomException(ResponseCodes.MISSING_FIELDS, "missing required parameter"),
    ResponseCodes.UNAUTHORIZED -> CustomException(ResponseCodes.UNAUTHORIZED, "Contraseña incorrecta. Inténtelo de nuevo"),
    ResponseCodes.USER_NOT_FOUND -> CustomException(ResponseCodes.USER_NOT_FOUND, "No se ha encontrado al usuario. Inténtelo de nuevo"),
    ResponseCodes.USUARIO_INACTIVO-> CustomException(ResponseCodes.USUARIO_INACTIVO, "Usuario inactivo. Contactarse con el administrador")
  )

  def get(code: Int): CustomException = errorsMap.getOrElse(code,genericError)
}
