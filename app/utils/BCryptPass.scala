package utils

import org.mindrot.jbcrypt.BCrypt

object BCryptPass {

  def createHashPass(password: String) = {
    val pwHash = BCrypt.hashpw(password, BCrypt.gensalt())
    pwHash
  }

  def validateHashPass(passwordUser: String, hashPassword: String) = {
    BCrypt.checkpw(passwordUser, hashPassword)
  }
}

