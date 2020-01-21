package dev.muha.okbase.writers

import com.zandero.rest.writer.HttpResponseWriter
import dev.muha.okbase.models.Permission
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse

class PermissionWriter : HttpResponseWriter<Permission> {
  override fun write(permission: Permission?, request: HttpServerRequest?, response: HttpServerResponse?) {
    if (permission != null) {
      response?.statusCode = 200
      response?.end(permission.toJson().toBuffer())
    } else {
      response?.statusCode = 500
      response?.end()
    }
  }
}
