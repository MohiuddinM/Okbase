package dev.muha.okbase.sql

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Documents : Table() {
  val id = uuid("id").primaryKey()
  val authorId = uuid("author_id") /*references Users.id*/ /* this can never be changed */
  val collection = varchar("collection", length = 32).nullable()
  val content = text("content")
  val deleted = bool("deleted").default(false)
  val createdAt = datetime("created_at")
  val modifiedAt = datetime("modified_at")
}
