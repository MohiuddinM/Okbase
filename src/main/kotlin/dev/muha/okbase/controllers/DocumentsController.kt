package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Delete
import com.zandero.rest.annotation.Get
import com.zandero.rest.annotation.Put
import dev.muha.okbase.models.Document
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.DocumentsRepository
import dev.muha.okbase.repos.PermissionsRepository
import dev.muha.okbase.utils.Gate
import dev.muha.okbase.utils.HttpStatus
import dev.muha.okbase.utils.Roles
import mu.KotlinLogging
import java.util.*
import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context

@Path("/documents")
class DocumentsController {
  private val log = KotlinLogging.logger {}
  private val permissionsRepository = PermissionsRepository()
  private val documentsRepository = DocumentsRepository(permissionsRepository)

  @GET
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun list(@Context user: User): List<Document> {
    log.info { "getting all documents" }
    return documentsRepository.getAllOfUser(user.id)
  }

  // listReadable
  // listWritable
  // listManageable

  @Get("/:id")
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun get(@PathParam("id") id: UUID, @Context user: User): Document {
    log.info { "getting document" }
    if (!Gate.canRead(user, documentId = id)) throw HttpStatus.unauthorized()
    return documentsRepository.get(id) ?: throw HttpStatus.notFound()
  }

  @POST
  @RolesAllowed(Roles.User, Roles.Admin)
  fun create(document: Document, @Context user: User): Document {
    log.info { "creating document for ${user.email}" }
    if (document.authorId != user.id) {
      throw HttpStatus.badRequest()
    }

    return documentsRepository.create(document) ?: throw HttpStatus.serverError()
  }

  @Put("/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun update(@PathParam("id") id: UUID, document: Document, @Context user: User): Document {
    log.info { "updating document" }
    if (document.id != id) {
      throw HttpStatus.badRequest()
    }

    if (!Gate.canWrite(user, documentId = id)) throw HttpStatus.unauthorized()

    return documentsRepository.update(document) ?: throw HttpStatus.serverError()
  }

  @Get("/:id/delete")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun hardDelete(@PathParam("id") id: UUID, @Context user: User): Document {
    if (!Gate.canManage(user, documentId = id)) throw HttpStatus.unauthorized()
    return documentsRepository.delete(id) ?: throw HttpStatus.serverError()
  }

  @Delete("/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun softDelete(@PathParam("id") id: UUID, @Context user: User): Document {
    if (!Gate.canManage(user, documentId = id)) throw HttpStatus.unauthorized()
    return documentsRepository.softDelete(id) ?: throw HttpStatus.serverError()
  }
}
