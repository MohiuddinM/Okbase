package dev.muha.okbase.writers

import com.zandero.rest.writer.HttpResponseWriter
import dev.muha.okbase.models.Document
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse

class DocumentWriter : HttpResponseWriter<Document> {
  override fun write(document: Document?, request: HttpServerRequest?, response: HttpServerResponse?) {
    if (document != null) {
      response?.statusCode = 200
      response?.end(document.toJson().toBuffer())
    } else {
      response?.statusCode = 500
      response?.end()
    }
  }
}
