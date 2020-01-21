package dev.muha.okbase.models

import dev.muha.okbase.sql.Users
import dev.muha.okbase.utils.Serializable
import dev.muha.okbase.utils.millis
import dev.muha.okbase.utils.toDateTime
import dev.muha.okbase.utils.toUuid
import io.vertx.core.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.Size


class User(
  val id: UUID,
  @get:Size(min = 3, max = 10) val role: String,
  @get:Size(min = 8, max = 60) val password: String?,
  @get:Email val email: String,
  val emailVerifiedAt: LocalDateTime?
) : Serializable {
  companion object {
    fun fromRow(row: ResultRow): User {
      return User(
        row[Users.id],
        row[Users.role],
        row[Users.password],
        row[Users.email],
        row[Users.emailVerifiedAt]
      )
    }

    fun fromJson(json: JsonObject): User {
      return User(
        json.getString("id").toUuid(),
        json.getString("role") ?: "User",
        json.getString("password"),
        json.getString("email"),
        json.getLong("emailVerifiedAt")?.toDateTime()
      )
    }
  }

  override fun toJson(): JsonObject {
    val json = JsonObject()
    json.put("id", id.toString())
    json.put("role", role)
    json.put("password", password)
    json.put("email", email)
    json.put("emailVerifiedAt", emailVerifiedAt?.millis)
    return json
  }
}
