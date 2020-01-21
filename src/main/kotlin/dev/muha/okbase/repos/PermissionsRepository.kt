package dev.muha.okbase.repos

import dev.muha.okbase.models.Document
import dev.muha.okbase.models.Permission
import dev.muha.okbase.sql.Permissions
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class PermissionsRepository {
  private val log = KotlinLogging.logger {}

  fun getAll(): List<Permission> {
    log.debug { "getting all permissions" }

    return transaction {
      Permissions.selectAll().map { Permission.fromRow(it) }
    }
  }

  fun getAllPermissionsOfUser(userId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.grantedTo.eq(userId) or Permissions.grantedBy.eq(userId) }.map { Permission.fromRow(it) }
    }
  }

  fun getAllPermissionsOfUserCreatedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Permission> {
    return transaction {
      Permissions.select { (Permissions.grantedTo.eq(userId) or Permissions.grantedBy.eq(userId)) and Permissions.createdAt.between(from, to) }.map { Permission.fromRow(it) }
    }
  }

  fun getAllPermissionsOfUserModifiedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Permission> {
    return transaction {
      Permissions.select { (Permissions.grantedTo.eq(userId) or Permissions.grantedBy.eq(userId)) and Permissions.modifiedAt.between(from, to) }.map { Permission.fromRow(it) }
    }
  }

  fun permissionsOfDocument(documentId: UUID): List<Permission> {
    log.debug { "getting all permissions of document: $documentId" }
    return transaction {
      Permissions.select { Permissions.documentId.eq(documentId) }.map { Permission.fromRow(it) }
    }
  }

  fun activePermissionsOfDocument(documentId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.documentId.eq(documentId) and Permissions.isActive.eq(true) and Permissions.expiresAt.greater(LocalDateTime.now()) }.map { Permission.fromRow(it) }
    }
  }

  fun permissionsGrantedToUser(userId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.grantedTo.eq(userId) }.map { Permission.fromRow(it) }
    }
  }

  fun activePermissionsGrantedToUser(userId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.grantedTo.eq(userId) and Permissions.isActive.eq(true) and Permissions.expiresAt.greater(LocalDateTime.now()) }.map { Permission.fromRow(it) }
    }
  }

  fun permissionsGrantedToUserOfDocument(userId: UUID, documentId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.documentId.eq(documentId) and Permissions.grantedTo.eq(userId) }.map { Permission.fromRow(it) }
    }
  }

  fun activePermissionsGrantedToUserOfDocument(userId: UUID, documentId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.documentId.eq(documentId) and Permissions.grantedTo.eq(userId) and Permissions.isActive.eq(true) and Permissions.expiresAt.greater(LocalDateTime.now()) }
        .map { Permission.fromRow(it) }
    }
  }

  fun permissionsGrantedByUser(userId: UUID): List<Permission> {
    return transaction {
      Permissions.select { Permissions.grantedBy.eq(userId) }.map { Permission.fromRow(it) }
    }
  }

  fun get(id: UUID): Permission? {
    return transaction {
      Permission.fromRow(Permissions.select { Permissions.id.eq(id) }.firstOrNull() ?: return@transaction null)
    }
  }

  fun create(permission: Permission): Permission? {
    log.debug { "creating permission: ${permission.id}" }
    return transaction {
      try {
        Permissions.insert {
          it[id] = permission.id
          it[documentId] = permission.documentId
          it[grantedBy] = permission.grantedBy
          it[grantedTo] = permission.grantedTo
          it[grantedToRole] = permission.grantedToRole
          it[canRead] = permission.canRead
          it[canWrite] = permission.canWrite
          it[canManage] = permission.canManage
          it[isActive] = permission.isActive
          it[expiresAt] = permission.expiresAt
          it[modifiedAt] = LocalDateTime.now()
          it[createdAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        log.warn { "permission creation failed: $e" }
        return@transaction null
      }

      return@transaction permission
    }
  }

  fun update(permission: Permission): Permission? {
    log.debug { "updating permission: ${permission.id}" }
    return transaction {
      try {
        Permissions.update({ Permissions.id.eq(permission.id) }) {
          it[documentId] = permission.documentId
          it[grantedBy] = permission.grantedBy
          it[grantedTo] = permission.grantedTo
          it[grantedToRole] = permission.grantedToRole
          it[canRead] = permission.canRead
          it[canWrite] = permission.canWrite
          it[canManage] = permission.canManage
          it[isActive] = permission.isActive
          it[expiresAt] = permission.expiresAt
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        log.warn { "permission update failed: $e" }
        return@transaction null
      }

      return@transaction permission
    }
  }

  fun deactivateExpired(): Boolean {
    return transaction {
      try {
        Permissions.update({ Permissions.expiresAt.lessEq(LocalDateTime.now()) }) {
          it[isActive] = false
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }

  fun deactivate(permissionId: UUID): Boolean {
    return transaction {
      try {
        Permissions.update({ Permissions.id.eq(permissionId) }) {
          it[isActive] = false
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }

  fun activate(permissionId: UUID): Boolean {
    return transaction {
      try {
        Permissions.update({ Permissions.id.eq(permissionId) }) {
          it[isActive] = true
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }

  fun delete(permission: Permission): Boolean {
    log.debug { "deleting permission: ${permission.id}" }
    return transaction {
      try {
        Permissions.deleteWhere { Permissions.id.eq(permission.id) }
      } catch (e: Exception) {
        log.warn { "permission delete failed: $e" }
        return@transaction false
      }

      return@transaction true
    }
  }

  fun deletePermissionsOfDocument(document: Document): Boolean {
    log.debug { "deleting all permissions of document: ${document.id}" }
    return transaction {
      try {
        Permissions.deleteWhere { Permissions.documentId.eq(document.id) }
      } catch (e: Exception) {
        log.warn { "permissions of document delete failed: $e" }
        return@transaction false
      }

      return@transaction true
    }
  }
}
