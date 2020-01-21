package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Delete
import com.zandero.rest.annotation.Post
import com.zandero.rest.annotation.Put
import dev.muha.okbase.models.Document
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.CollectionsRepository
import dev.muha.okbase.utils.Gate
import dev.muha.okbase.utils.HttpStatus
import dev.muha.okbase.utils.Roles
import java.util.*
import javax.annotation.security.RolesAllowed
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context

@Path("/collections")
class CollectionsController {
  @Post("/:collection")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun create(@PathParam("collection") collection: String, document: Document, @Context user: User): Boolean {
    if (user.id != document.authorId) {
      throw HttpStatus.unauthorized()
    }

    return CollectionsRepository.addToCollection(collection, document)
  }

  @Put("/:collection/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun update(@PathParam("collection") collection: String, @PathParam("id") id: UUID, document: Document, @Context user: User): Boolean {
    if (document.id != id) {
      throw HttpStatus.badRequest()
    }

    if (!Gate.canWrite(user, documentId = id)) throw HttpStatus.unauthorized()

    return CollectionsRepository.updateInCollection(collection, document)
  }

  @Delete("/:collection/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun delete(@PathParam("collection") collection: String, @PathParam("id") id: UUID, @Context user: User): Boolean {
    if (!Gate.canManage(user, documentId = id)) throw HttpStatus.unauthorized()
    return CollectionsRepository.deleteFromCollection(collection, id)
  }


}
