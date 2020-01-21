package dev.muha.okbase.models

import dev.muha.okbase.sql.Documents
import dev.muha.okbase.utils.Serializable
import dev.muha.okbase.utils.millis
import dev.muha.okbase.utils.toDateTime
import dev.muha.okbase.utils.toUuid
import io.vertx.core.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime
import java.util.*

class Document(
  val id: UUID,
  val authorId: UUID,
  val collection: String?,
  val content: String,
  val deleted: Boolean,
  val modifiedAt: LocalDateTime,
  val createdAt: LocalDateTime
) : Serializable {

  companion object {
    fun fromRow(row: ResultRow): Document {
      return Document(
        row[Documents.id],
        row[Documents.authorId],
        row[Documents.collection],
        row[Documents.content],
        row[Documents.deleted],
        row[Documents.modifiedAt],
        row[Documents.createdAt]
      )
    }

    fun fromJson(json: JsonObject): Document {
      return Document(
        json.getString("id").toUuid(),
        json.getString("authorId").toUuid(),
        json.getString("collection"),
        json.getString("content"),
        json.getBoolean("deleted", false),
        json.getLong("modifiedAt").toDateTime(),
        json.getLong("createdAt").toDateTime()
      )
    }
  }


  override fun toJson(): JsonObject {
    val json = JsonObject()
    json.put("id", id.toString())
    json.put("authorId", authorId.toString())
    json.put("collection", collection)
    json.put("content", if (deleted) "" else content)
    json.put("deleted", deleted)
    json.put("modifiedAt", modifiedAt.millis)
    json.put("createdAt", createdAt.millis)
    return json
  }
}
