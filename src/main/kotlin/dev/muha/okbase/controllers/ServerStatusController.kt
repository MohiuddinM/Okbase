package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Get
import dev.muha.okbase.sql.Users
import io.vertx.core.Promise
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import javax.annotation.security.PermitAll
import javax.ws.rs.Path

@Path("/server-status")
class ServerStatusController {
  private val log = KotlinLogging.logger {}

  @Get("/controller")
  @PermitAll
  fun test(): String {
    return "OK"
  }

  @Get("/db")
  @PermitAll
  fun testDb(): Promise<Boolean> {
    val result = Promise.promise<Boolean>()
    runBlocking {
      transaction {
        result.complete(Users.exists())
      }
    }
    return result
  }
}
