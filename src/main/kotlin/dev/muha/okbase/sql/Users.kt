package dev.muha.okbase.sql

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Users : Table() {
  val id = uuid("id").primaryKey()
  val role = varchar("role", length = 20)
  val password = varchar("password", length = 64)
  val email = varchar("email", length = 60).uniqueIndex()
  val emailVerifiedAt = datetime("email_verified_at").nullable()
  val createdAt = datetime("created_at")
  val modifiedAt = datetime("modified_at")
}
