package dev.muha.okbase.writers

import com.zandero.rest.writer.HttpResponseWriter
import dev.muha.okbase.models.User
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse

class UserWriter : HttpResponseWriter<User> {
  override fun write(user: User?, request: HttpServerRequest?, response: HttpServerResponse?) {
    if (user != null) {
      response?.statusCode = 200
      response?.end(user.toJson().toBuffer())
    } else {
      response?.statusCode = 500
      response?.end()
    }
  }
}
