package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Get
import dev.muha.okbase.models.DocumentChange
import dev.muha.okbase.models.PermissionChange
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.DocumentsRepository
import dev.muha.okbase.repos.PermissionsRepository
import dev.muha.okbase.utils.ChangeType
import mu.KotlinLogging
import javax.ws.rs.Path
import javax.ws.rs.core.Context

@Path("/sync")
class SyncController {
  private val log = KotlinLogging.logger {}
  private val permissionsRepository = PermissionsRepository()
  private val documentsRepository = DocumentsRepository(permissionsRepository)

  @Get("/permission-changes/all")
  fun permissionChangesAll(@Context user: User): List<PermissionChange> {
    val permissions = permissionsRepository.activePermissionsGrantedToUser(user.id)

    return permissions.map {
      if (it.createdAt == it.modifiedAt) PermissionChange(ChangeType.create, it.id, createdAt = it.createdAt)
      else PermissionChange(ChangeType.update, it.id, modifiedAt = it.modifiedAt)
    }
  }

  @Get("/document-changes/all")
  fun documentChangesAll(@Context user: User): List<DocumentChange> {
    val documents = documentsRepository.getAllDocumentsAllowedToUser(user.id)

    return documents.map {
      when {
        it.deleted -> DocumentChange(ChangeType.delete, it.id, deletedAt = it.modifiedAt)
        it.createdAt == it.modifiedAt -> DocumentChange(ChangeType.create, it.id, createdAt = it.createdAt)
        else -> DocumentChange(ChangeType.update, it.id, modifiedAt = it.modifiedAt)
      }
    }
  }
}
