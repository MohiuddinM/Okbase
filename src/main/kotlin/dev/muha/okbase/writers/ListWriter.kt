package dev.muha.okbase.writers

import com.zandero.rest.writer.HttpResponseWriter
import dev.muha.okbase.models.User
import dev.muha.okbase.utils.Serializable
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse

class ListWriter : HttpResponseWriter<ArrayList<Any>> {
  override fun write(list: ArrayList<Any>, request: HttpServerRequest?, response: HttpServerResponse?) {
    if (list.isEmpty()) {
      response?.statusCode = 200
      response?.end()
    } else {
      val listStrings = list.map {
        if (it is Serializable) {
          val json = it.toJson()

          if (it is User) json.remove("password")

          json.toString()
        } else it.toString()

      }.toString()

      response?.statusCode = 200
      response?.end(listStrings)
    }
  }
}
