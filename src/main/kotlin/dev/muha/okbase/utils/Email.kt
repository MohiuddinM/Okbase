package dev.muha.okbase.utils

import dev.muha.okbase.Config
import dev.muha.okbase.models.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import mu.KotlinLogging

private val log = KotlinLogging.logger {}

fun User.sendVerificationEmail(code: String) {
  this.email(
    "",
    "http://${Config.appHost}:${Config.appPort}/verify/email?email=${this.email}&code=$code"
  )
}

fun User.email(from: String, text: String, html: String = ""): Deferred<Boolean> {
  return email(from, this.email, text, html)
}

fun email(from: String, to: String, text: String, html: String): Deferred<Boolean> {
  log.info { "email sent: $text" }
  return GlobalScope.async { true }
}

fun User.sendPasswordResetEmail(code: String) {
  this.email("", "You have requested a password reset: http://${Config.appHost}:${Config.appPort}/change-password?code=$code")
}
