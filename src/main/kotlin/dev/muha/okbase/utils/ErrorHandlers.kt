package dev.muha.okbase.utils

import com.zandero.rest.exception.ExceptionHandler
import io.vertx.core.http.HttpServerRequest
import io.vertx.core.http.HttpServerResponse


class HttpStatus(val statusCode: Int, override val message: String) : Exception() {
  companion object {
    fun ok(message: String = "OK"): HttpStatus {
      return HttpStatus(200, message)
    }

    fun notFound(message: String = "Not Found"): HttpStatus {
      return HttpStatus(404, message)
    }

    fun serverError(message: String = "Server Error"): HttpStatus {
      return HttpStatus(500, message)
    }

    fun conflict(message: String = "Conflict"): HttpStatus {
      return HttpStatus(409, message)
    }

    fun unauthorized(message: String = "Unauthorized"): HttpStatus {
      return HttpStatus(401, message)
    }

    fun badRequest(message: String = "Bad Request"): HttpStatus {
      return HttpStatus(400, message)
    }

    fun forbidden(message: String = "Forbidden"): HttpStatus {
      return HttpStatus(403, message)
    }
  }
}

class HttpStatusExceptionHandler : ExceptionHandler<HttpStatus> {
  override fun write(result: HttpStatus, request: HttpServerRequest, response: HttpServerResponse) {
    response.statusCode = result.statusCode
    response.end(result.message)
  }
}

class GenericExceptionHandler : ExceptionHandler<Exception> {
  override fun write(result: Exception, request: HttpServerRequest, response: HttpServerResponse) {
    response.statusCode = 500
    response.end(result.message)
  }
}
