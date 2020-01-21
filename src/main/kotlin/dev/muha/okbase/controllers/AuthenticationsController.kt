package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Get
import com.zandero.rest.annotation.Post
import dev.muha.okbase.repos.UsersRepository
import dev.muha.okbase.utils.*
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.ws.rs.core.Context

class AuthenticationsController {
  private val log = KotlinLogging.logger {}
  private val usersRepository = UsersRepository()

  @Post("/login")
  @PermitAll
  fun login(authData: String): Future<String> {
    log.info { "logging user in" }
    val data = JsonObject(authData)
    val email = data.getString("email", "")
    val password = data.getString("password", "")

    if (email.isBlank() || password.isBlank()) {
      throw HttpStatus.badRequest()
    }

    val result = Future.future<String>()

    runBlocking {

      val user = usersRepository.getByEmail(email) ?: throw HttpStatus.badRequest()

      if (!user.password.verifyBcryptHash(password)) throw HttpStatus.badRequest()


//      if (row[Users.emailVerifiedAt] == null) {
//        throw HttpStatus.conflict("email confirmation pending")
//      }

      val jwtUser = JwtUser(user.id, user.role)
      val token = JwtAuth.generateToken(jwtUser.principal())
      result.complete(token)
    }

    return result
  }

  @Get("/logout")
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun logout(@Context context: RoutingContext) {
    context.clearUser()
    // ToDo: Probably more work needed
  }

  @Post("/password/forgot-password")
  @PermitAll
  fun forgotPassword(contact: String) {
    val json = JsonObject(contact)
    val email = json.getString("email", "")

    if (email.isBlank()) {
      throw HttpStatus.badRequest()
    }

    runBlocking {
      val user = usersRepository.getByEmail(email) ?: throw HttpStatus.badRequest()

      val code = passwordResetTokens.get(email) ?: throw HttpStatus.serverError()
      user.sendPasswordResetEmail(code)
    }
  }

  @Post("/password/verify-code")
  @PermitAll
  fun verifyCode(data: String) {
    // ToDo: throttle this function
    val json = JsonObject(data)
    val code = json.getString("code", "")
    val email = json.getString("email", "")

    if (email.isBlank() || code.isBlank()) {
      throw HttpStatus.badRequest()
    }

    if (passwordResetTokens.getIfPresent(email) == null) throw HttpStatus.badRequest()
  }

  @Post("/password/reset")
  @PermitAll
  fun passwordReset(data: String): Boolean {
    val json = JsonObject(data)
    val password = json.getString("password", "")
    val code = json.getString("code", "")
    val email = json.getString("email", "")

    if (password.isBlank() || code.isBlank()) {
      throw HttpStatus.badRequest()
    }

    if (email.isBlank()) {
      throw HttpStatus.badRequest()
    }

    if (passwordResetTokens.getIfPresent(email) != code) {
      throw HttpStatus.badRequest()
    }

    val updated = usersRepository.updatePasswordByEmail(email, password.toBcryptHash())

    if (updated) passwordResetTokens.invalidate(email)

    return updated
  }
}
