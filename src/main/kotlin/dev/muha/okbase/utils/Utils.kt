package dev.muha.okbase.utils

import io.vertx.core.Vertx
import mu.KotlinLogging
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.validation.Validation
import javax.validation.Validator
import kotlin.random.Random


val validator: Validator = Validation.buildDefaultValidatorFactory().validator


fun abortUnless(condition: Boolean, error: HttpStatus = HttpStatus.unauthorized()) {
  if (!condition) throw error
}

fun String.toUuid(): UUID {
  return UUID.fromString(this)
  //return if (this.isNullOrEmpty()) null else UUID.fromString(this)
}

fun String.toUuidOrNull(): UUID? {
  return try {
    UUID.fromString(this)
  } catch (e: Exception) {
    null
  }
}

fun Long.toDateTime(): LocalDateTime {
  val instant = Instant.ofEpochMilli(this / 1000)
  return instant.atZone(ZoneOffset.UTC).toLocalDateTime()
}

fun Int.toDateTime(): LocalDateTime {
  return this.toLong().toDateTime()
}

fun String.toDateTime(): LocalDateTime {
  return LocalDateTime.parse(this)
}

val LocalDateTime.millis: Long
  get() = this.toInstant(ZoneOffset.UTC).toEpochMilli()

fun genRandomString(length: Int = 10, numericOnly: Boolean = false): String {
  val charPool = if (numericOnly) {
    ('0'..'9').toList()
  } else {
    ('a'..'z') + ('A'..'Z') + ('0'..'9')
  }

  return (1..length)
    .map { Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("")
}

internal object Utils {
  private val vertx = Vertx.currentContext().owner()

  private val log = KotlinLogging.logger {}
}
