package dev.muha.okbase.models

import dev.muha.okbase.sql.Permissions
import dev.muha.okbase.utils.Serializable
import dev.muha.okbase.utils.millis
import dev.muha.okbase.utils.toDateTime
import dev.muha.okbase.utils.toUuid
import io.vertx.core.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime
import java.util.*

class Permission(
  val id: UUID,
  val documentId: UUID,
  val grantedBy: UUID,
  val grantedTo: UUID?,
  val grantedToRole: String?,
  val canRead: Boolean = false,
  val canWrite: Boolean = false,
  val canManage: Boolean = false,
  val isActive: Boolean = true,
  val expiresAt: LocalDateTime?,
  val modifiedAt: LocalDateTime? = null,
  val createdAt: LocalDateTime? = null
) : Serializable {


  companion object {
    fun fromRow(row: ResultRow): Permission {
      return Permission(
        row[Permissions.id],
        row[Permissions.documentId],
        row[Permissions.grantedBy],
        row[Permissions.grantedTo],
        row[Permissions.grantedToRole],
        row[Permissions.canRead],
        row[Permissions.canWrite],
        row[Permissions.canManage],
        row[Permissions.isActive],
        row[Permissions.expiresAt],
        row[Permissions.modifiedAt],
        row[Permissions.createdAt]
      )
    }

    fun fromJson(json: JsonObject): Permission {
      return Permission(
        json.getString("id").toUuid(),
        json.getString("objectId").toUuid(),
        json.getString("grantedBy").toUuid(),
        json.getString("grantedTo")?.toUuid(),
        json.getString("grantedToRole"),
        json.getBoolean("canRead"),
        json.getBoolean("canWrite"),
        json.getBoolean("canManage"),
        json.getBoolean("isActive"),
        json.getString("expiresAt")?.toDateTime()
      )
    }
  }


  override fun toJson(): JsonObject {
    val json = JsonObject()
    json.put("id", id.toString())
    json.put("documentId", documentId.toString())
    json.put("grantedBy", grantedBy.toString())
    json.put("grantedTo", grantedTo.toString())
    json.put("grantedToRole", grantedToRole.toString())
    json.put("canRead", canRead.toString())
    json.put("canWrite", canWrite.toString())
    json.put("canManage", canManage)
    json.put("isActive", isActive)
    json.put("expiresAt", expiresAt?.millis)
    return json
  }

}
