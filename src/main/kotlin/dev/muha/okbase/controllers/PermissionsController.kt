package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Delete
import com.zandero.rest.annotation.Get
import com.zandero.rest.annotation.Put
import dev.muha.okbase.models.Permission
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.PermissionsRepository
import dev.muha.okbase.utils.Gate
import dev.muha.okbase.utils.HttpStatus
import dev.muha.okbase.utils.Roles
import java.util.*
import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context

@Path("/permissions")
class PermissionsController {
  private val permissionsRepository = PermissionsRepository()

  @GET
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun list(@Context user: User): List<Permission> {
    return permissionsRepository.permissionsGrantedByUser(user.id)
  }

  @POST
  @RolesAllowed(Roles.User, Roles.Admin)
  fun create(permission: Permission, @Context user: User): Permission {
    if (!Gate.canManage(user, documentId = permission.documentId)) {
      throw HttpStatus.unauthorized()
    }

    if (permission.grantedTo != null && permission.grantedToRole != null) {
      throw HttpStatus.conflict("Permission can be granted either to a user or a role")
    }

    return permissionsRepository.create(permission) ?: throw HttpStatus.notFound()
  }

  @Get("/:id")
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun get(@PathParam("id") id: UUID, @Context user: User): Permission {
    val perm = permissionsRepository.get(id) ?: throw HttpStatus.notFound()

    /* Granted to user can see the permission even if it is expired */
    return if (perm.grantedBy == user.id || perm.grantedTo == user.id) perm else throw HttpStatus.unauthorized()
  }

  @Put("/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun update(@PathParam("id") id: UUID, permission: Permission, @Context user: User) {
    throw HttpStatus.serverError()
  }

  @Get("/:id/deactivate")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun deactivate(@PathParam("id") id: UUID, @Context user: User) {
    val perm = permissionsRepository.get(id) ?: throw HttpStatus.notFound()

    if (perm.grantedBy != user.id) throw HttpStatus.unauthorized()

    permissionsRepository.deactivate(id)
  }

  @Get("/:id/deactivate")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun activate(@PathParam("id") id: UUID, @Context user: User) {
    val perm = permissionsRepository.get(id) ?: throw HttpStatus.notFound()

    if (perm.grantedBy != user.id) throw HttpStatus.unauthorized()

    permissionsRepository.activate(id)
  }

  @Delete("/:id")
  @RolesAllowed(Roles.User, Roles.Admin)
  fun delete(@PathParam("id") id: UUID, @Context user: User) {
    val perm = permissionsRepository.get(id) ?: throw HttpStatus.notFound()

    if (perm.grantedBy == user.id) {
      permissionsRepository.delete(perm)
    } else throw HttpStatus.unauthorized()
  }
}
