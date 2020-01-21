package dev.muha.okbase.utils

import dev.muha.okbase.models.Document
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.DocumentsRepository
import dev.muha.okbase.repos.PermissionsRepository
import java.util.*

// ToDo: implement roles

object Gate {
  private val permissionsRepository = PermissionsRepository()
  private val documentsRepository = DocumentsRepository(permissionsRepository)

  fun canRead(user: User, document: Document? = null, documentId: UUID? = null): Boolean {
    val doc: Document = if (document == null) {
      if (documentId == null) return false
      val d = documentsRepository.get(documentId) ?: return false
      d

    } else document


    if (doc.authorId == user.id || user.role == Roles.Admin || user.role == Roles.ReadOnlyAdmin) return true

    val perms = permissionsRepository.activePermissionsGrantedToUserOfDocument(user.id, doc.id)

    return perms.isNotEmpty()
  }

  fun canWrite(user: User, document: Document? = null, documentId: UUID? = null): Boolean {
    val doc: Document = if (document == null) {
      if (documentId == null) return false
      val d = documentsRepository.get(documentId) ?: return false
      d

    } else document


    if (doc.authorId == user.id || user.role == Roles.Admin) return true


    return permissionsRepository.activePermissionsGrantedToUserOfDocument(user.id, doc.id).find { it.canWrite || it.canManage } != null
  }

  fun canManage(user: User, document: Document? = null, documentId: UUID? = null): Boolean {
    val doc: Document = if (document == null) {
      if (documentId == null) return false
      val d = documentsRepository.get(documentId) ?: return false
      d

    } else document


    if (doc.authorId == user.id || user.role == Roles.Admin) return true


    return permissionsRepository.activePermissionsGrantedToUserOfDocument(user.id, doc.id).find { it.canManage } != null
  }
}
