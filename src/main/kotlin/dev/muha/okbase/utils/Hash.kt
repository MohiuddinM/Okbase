package dev.muha.okbase.utils

import at.favre.lib.crypto.bcrypt.BCrypt
import java.security.MessageDigest

fun String.sha1() = HashUtils.sha1(this)
fun String.sha256() = HashUtils.sha256(this)

fun String.toBcryptHash(cost: Int = 12): String {
  return BCrypt.withDefaults().hashToString(cost, this.toCharArray())
}

fun String?.verifyBcryptHash(password: String): Boolean {
  if (this == null) return false
  return BCrypt.verifyer().verify(password.toCharArray(), this).verified
}

object HashUtils {
  fun sha512(input: String) = hashString("SHA-512", input)

  fun sha256(input: String) = hashString("SHA-256", input)

  fun sha1(input: String) = hashString("SHA-1", input)

  private fun hashString(type: String, input: String): String {
    val hexChars = "0123456789ABCDEF"
    val bytes = MessageDigest
      .getInstance(type)
      .digest(input.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
      val i = it.toInt()
      result.append(hexChars[i shr 4 and 0x0f])
      result.append(hexChars[i and 0x0f])
    }

    return result.toString()
  }
}
