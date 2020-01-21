package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Get
import dev.muha.okbase.models.User
import dev.muha.okbase.sql.Users
import dev.muha.okbase.utils.Roles
import dev.muha.okbase.utils.emailCodesCache
import dev.muha.okbase.utils.genRandomString
import dev.muha.okbase.utils.sendVerificationEmail
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context

class VerificationsController {
  @Get("/verify/resend-email")
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun resendVerificationEmail(@Context user: User) {
    if (emailCodesCache.getIfPresent(user.email) != null) {
      emailCodesCache.invalidate(user.email)
    }

    val randomString = genRandomString()

    emailCodesCache.put(user.email, randomString)
    user.sendVerificationEmail(randomString)
  }

  @Get("/verify/resend-sms")
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun resendVerificationSms(@Context user: User) {

  }

  @Get("/verify/email")
  @PermitAll
  fun emailVerify(@PathParam("email") email: String, @PathParam("code") code: String): String {
    return if (emailCodesCache.getIfPresent(email) == code) {
      transaction {
        Users.update({ Users.email.eq(email) }) {
          it[emailVerifiedAt] = LocalDateTime.now()
          it[modifiedAt] = LocalDateTime.now()
        }
      }
      emailCodesCache.invalidate(email)
      "Email has been verified"
    } else "Email could not be verified"
  }
}
