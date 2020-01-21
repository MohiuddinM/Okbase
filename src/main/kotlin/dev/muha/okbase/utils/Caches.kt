package dev.muha.okbase.utils

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import dev.muha.okbase.models.User
import dev.muha.okbase.sql.Users
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

val usersCache: LoadingCache<UUID, Deferred<User>> = Caffeine.newBuilder()
  .maximumSize(100)
  .expireAfterWrite(1, TimeUnit.MINUTES)
  .build { id ->
    GlobalScope.async {
      val row = transaction {
        Users.select { Users.id.eq(id) }.first()
      }
      User.fromRow(row)
    }
  }

val emailCodesCache: Cache<String, String> = Caffeine.newBuilder()
  .expireAfterWrite(Duration.ofHours(1))
  .build()

val passwordResetTokens: LoadingCache<String, String> = Caffeine.newBuilder()
  .expireAfterWrite(Duration.ofHours(1))
  .build { genRandomString(length = 5, numericOnly = true) }
