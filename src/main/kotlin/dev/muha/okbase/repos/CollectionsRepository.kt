package dev.muha.okbase.repos

import dev.muha.okbase.models.Document
import dev.muha.okbase.sql.Documents
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.*

object CollectionsRepository {
  fun create(name: String): Boolean {
    return false
  }

  fun delete(name: String): Boolean {
    return false
  }

  fun addToCollection(collectionName: String, document: Document): Boolean {
    return transaction {
      try {
        Documents.insert {
          it[id] = document.id
          it[authorId] = document.authorId
          it[collection] = collectionName
          it[content] = document.content
          it[modifiedAt] = LocalDateTime.now()
          it[createdAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }

  fun deleteFromCollection(collection: String, documentId: UUID): Boolean {
    return transaction {
      try {
        Documents.deleteWhere { Documents.id.eq(documentId) }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }

  fun updateInCollection(collection: String, document: Document): Boolean {
    return transaction {
      try {
        Documents.update({ Documents.id.eq(document.id) }) {
          it[content] = document.content
          it[modifiedAt] = LocalDateTime.now()
        }
      } catch (e: Exception) {
        return@transaction false
      }

      return@transaction true
    }
  }
}
