package dev.muha.okbase.utils

import com.zandero.rest.context.ContextProviderFactory
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.jwt.JWTOptions
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging


private val log = KotlinLogging.logger {}

object JwtAuth {

  private val jwtProvider: JWTAuth = JWTAuth.create(
    Vertx.currentContext().owner(), JWTAuthOptions()
      .addPubSecKey(
        PubSecKeyOptions()
          .setAlgorithm("HS256")
          .setPublicKey(dev.muha.okbase.Config.jwtSecretKey)
          .setSymmetric(true)
      )
  )

  fun generateToken(principal: JsonObject): String {
    val options = json {
      obj {
        "issuer" to dev.muha.okbase.Config.jwtIssuer
        "expiresInSeconds" to dev.muha.okbase.Config.jwtValidity
      }
    }

    return jwtProvider.generateToken(principal, JWTOptions(options))
  }

  fun handler(): Handler<RoutingContext> {
    return Handler { context ->
      val token: String? = context.request().getHeader("Authorization")?.substring(7)
      log.info { "token: $token" }
      if (!token.isNullOrBlank()) {
        jwtProvider.authenticate(JsonObject().put("jwt", token)) { auth ->
          if (auth.succeeded()) {
            val principal = auth.result().principal()
            val jwtUser = JwtUser(principal.getString("id").toUuid(), principal.getString("role"))

            runBlocking {
              val user = usersCache.get(jwtUser.id)?.await()

              if (user != null) {
                context.put(ContextProviderFactory.getContextKey(user), user)
                context.setUser(jwtUser)
              } else {
                log.warn { "auth failed: user not found" }
              }

              context.next()
            }
          } else {
            log.warn { "token auth failed: ${auth.cause()}" }
            context.next()
          }
        }
      } else {
        log.warn { "auth failed: token not provided" }
        context.next()
      }
    }
  }
}
