package dev.muha.okbase.readers

import com.zandero.rest.reader.ValueReader
import dev.muha.okbase.models.Permission
import dev.muha.okbase.utils.validator
import io.vertx.core.json.JsonObject
import mu.KotlinLogging

class PermissionReader : ValueReader<Permission> {
  private val log = KotlinLogging.logger {}


  override fun read(value: String?, type: Class<Permission>?): Permission {
    val permission = Permission.fromJson(JsonObject(value))

    val constraintViolations = validator.validate(permission)

    return if (constraintViolations.isEmpty()) {
      permission
    } else {
      for (violation in constraintViolations) {
        log.info("${violation.propertyPath}: ${violation.message}")
      }

      throw IllegalArgumentException(constraintViolations.map { "${it.propertyPath}: ${it.message}" }.reduceRight { s, acc -> "$acc \n $s" })
    }
  }
}
