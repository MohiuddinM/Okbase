package dev.muha.okbase.utils

import io.vertx.core.AsyncResult
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.AbstractUser
import io.vertx.ext.auth.AuthProvider
import java.util.*

class JwtUser(val id: UUID, val role: String) : AbstractUser() {
  override fun setAuthProvider(authProvider: AuthProvider?) {
    TODO("not implemented")
  }

  override fun principal(): JsonObject {
    return toJson()
  }

  override fun doIsPermitted(permission: String?, resultHandler: Handler<AsyncResult<Boolean>>?) {
    resultHandler?.handle(Future.succeededFuture(role == permission))
  }

  fun toJson(): JsonObject {
    return JsonObject().put("id", id.toString()).put("role", role)
  }
}
