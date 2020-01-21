package dev.muha.okbase.readers

import com.zandero.rest.reader.ValueReader
import dev.muha.okbase.models.Document
import dev.muha.okbase.utils.validator
import io.vertx.core.json.JsonObject
import mu.KotlinLogging

class DocumentReader : ValueReader<Document> {
  private val log = KotlinLogging.logger {}


  override fun read(value: String?, type: Class<Document>?): Document {
    val document = Document.fromJson(JsonObject(value))

    val constraintViolations = validator.validate(document)

    return if (constraintViolations.isEmpty()) {
      document
    } else {
      for (violation in constraintViolations) {
        log.info("${violation.propertyPath}: ${violation.message}")
      }

      throw IllegalArgumentException(constraintViolations.map { "${it.propertyPath}: ${it.message}" }.reduceRight { s, acc -> "$acc \n $s" })
    }
  }
}
