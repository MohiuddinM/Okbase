package dev.muha.okbase.models

import dev.muha.okbase.utils.Serializable
import dev.muha.okbase.utils.millis
import io.vertx.core.json.JsonObject
import java.time.LocalDateTime
import java.util.*

class DocumentChange(
  val type: String,
  val documentId: UUID,
  val createdAt: LocalDateTime? = null,
  val modifiedAt: LocalDateTime? = null,
  val deletedAt: LocalDateTime? = null
) : Serializable {
  override fun toJson(): JsonObject {
    val json = JsonObject()
    json.put("type", type)
    json.put("documentId", documentId.toString())
    if (createdAt != null) json.put("createdAt", createdAt.millis)
    if (modifiedAt != null) json.put("modifiedAt", modifiedAt.millis)
    if (deletedAt != null) json.put("deletedAt", deletedAt.millis)
    return json
  }
}
