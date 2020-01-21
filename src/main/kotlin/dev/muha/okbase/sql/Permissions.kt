package dev.muha.okbase.sql

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.datetime

object Permissions : Table() {
  val id = uuid("id").primaryKey()
  val documentId = uuid("document_id") /*references Documents.id*/
  val grantedBy = uuid("granted_by") /*references Users.id*/
  val grantedTo = (uuid("granted_to") /*references Users.id*/).nullable()
  val grantedToRole = varchar("grantedToRole", length = 20).nullable()          /* If user (granted to) is no longer at the role then permission will not be granted */
  val canRead = bool("can_read").default(false)
  val canWrite = bool("can_write").default(false)
  val canManage = bool("can_manage").default(false) /* This permission allows to manage permissions and delete */
  val isActive = bool("is_active").default(true)
  val expiresAt = datetime("expires_at").nullable()
  val modifiedAt = datetime("modified_at")
  val createdAt = datetime("created_at")
}
