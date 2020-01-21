package dev.muha.okbase.repos

import dev.muha.okbase.models.Document
import dev.muha.okbase.sql.Documents
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class DocumentsRepository(private val permissionsRepository: PermissionsRepository) {
  fun getAll(): List<Document> {
    return transaction {
      Documents.selectAll().map { Document.fromRow(it) }
    }
  }

  fun getAllOfUser(authorId: UUID): List<Document> {
    return transaction {
      Documents.select { Documents.authorId.eq(authorId) }.map { Document.fromRow(it) }
    }
  }

  fun getAllDocumentsOfUserCreatedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Document> {
    return transaction {
      Documents.select { Documents.authorId.eq(userId) and Documents.createdAt.between(from, to) }.map { Document.fromRow(it) }
    }
  }

  fun getAllDocumentsOfUserModifiedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Document> {
    return transaction {
      Documents.select { Documents.authorId.eq(userId) and Documents.modifiedAt.between(from, to) }.map { Document.fromRow(it) }
    }
  }

  fun getAllDocumentsAllowedToUser(userId: UUID): List<Document> {
    return transaction {
      Documents.select { Documents.id.inList(permissionsRepository.activePermissionsGrantedToUser(userId).map { it.documentId }) }.map { Document.fromRow(it) }
    }
  }

  fun getAllDocumentsAllowedToUserCreatedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Document> {
    return transaction {
      Documents.select { Documents.id.inList(permissionsRepository.activePermissionsGrantedToUser(userId).map { it.documentId }) and Documents.createdAt.between(from, to) }.map { Document.fromRow(it) }
    }
  }

  fun getAllDocumentsAllowedToUserModifiedBetween(userId: UUID, from: LocalDateTime, to: LocalDateTime): List<Document> {
    return transaction {
      Documents.select { Documents.id.inList(permissionsRepository.activePermissionsGrantedToUser(userId).map { it.documentId }) and Documents.modifiedAt.between(from, to) }.map { Document.fromRow(it) }
    }
  }

  fun get(documentId: UUID): Document? {
    return transaction {
      Document.fromRow(Documents.select { Documents.id.eq(documentId) and Documents.deleted.eq(false) }.firstOrNull() ?: return@transaction null)
    }
  }

  fun create(document: Document): Document? {
    return transaction {
      try {
        Documents.insert {
          it[id] = document.id
          it[authorId] = document.authorId
          it[collection] = document.collection
          it[content] = document.content
          it[modifiedAt] = LocalDateTime.now()
          it[createdAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        print(e.toString())
        return@transaction null
      }

      return@transaction document
    }
  }

  fun update(document: Document): Document? {
    println("updating document: ${document.content}")
    return transaction {
      try {
        Documents.update({ Documents.id.eq(document.id) }) {
          it[content] = document.content
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction null
      }

      return@transaction document
    }
  }

  fun softDelete(documentId: UUID): Document? {
    return transaction {
      try {
        Documents.update({ Documents.id.eq(documentId) }) {
          it[deleted] = true
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction null
      }

      return@transaction get(documentId)
    }
  }

  fun delete(documentId: UUID): Document? {
    return transaction {
      try {
        Documents.deleteWhere { Documents.id.eq(documentId) }
      } catch (e: Exception) {
        return@transaction null
      }

      return@transaction get(documentId)
    }
  }
}
