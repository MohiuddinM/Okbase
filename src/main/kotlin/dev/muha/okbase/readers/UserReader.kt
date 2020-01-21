package dev.muha.okbase.readers

import com.zandero.rest.reader.ValueReader
import dev.muha.okbase.models.User
import dev.muha.okbase.utils.validator
import io.vertx.core.json.JsonObject
import mu.KotlinLogging

class UserReader : ValueReader<User> {
  private val log = KotlinLogging.logger {}


  override fun read(value: String?, type: Class<User>?): User {
    val user = User.fromJson(JsonObject(value))

    val constraintViolations = validator.validate(user)

    return if (constraintViolations.isEmpty()) {
      user
    } else {
      for (violation in constraintViolations) {
        log.info("${violation.propertyPath}: ${violation.message}")
      }

      throw IllegalArgumentException(constraintViolations.map { "${it.propertyPath}: ${it.message}" }.reduceRight { s, acc -> "$acc \n $s" })
    }
  }
}
