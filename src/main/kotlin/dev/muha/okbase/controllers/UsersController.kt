package dev.muha.okbase.controllers

import com.zandero.rest.annotation.Post
import dev.muha.okbase.models.User
import dev.muha.okbase.repos.UsersRepository
import dev.muha.okbase.utils.*
import io.vertx.core.Promise
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import java.util.*
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.ws.rs.*
import javax.ws.rs.core.Context


@Path("/user")
class UsersController {
  private val log = KotlinLogging.logger {}
  private val usersRepository = UsersRepository()


  @GET
  @RolesAllowed(Roles.User, Roles.Admin, Roles.ReadOnlyAdmin)
  fun me(@Context user: User): User {
    log.info { "++ me ++" }
    return user
  }

  @POST
  @PermitAll
  fun create(user: User): Promise<Boolean> {
    log.info { "creating new account" }
    val result = Promise.promise<Boolean>()

    runBlocking {
      if (usersRepository.store(user)) {
        val randomString = genRandomString()
        emailCodesCache.put(user.email, randomString)
        user.sendVerificationEmail(randomString)
        result.complete(true)
      } else throw HttpStatus.serverError()
    }

    return result
  }

  @PUT
  @RolesAllowed(Roles.User, Roles.Admin)
  fun update(newUser: User, @Context oldUser: User): Promise<Unit> {
    if (newUser.id != oldUser.id) {
      throw HttpStatus.conflict()
    }

    val result = Promise.promise<Unit>()

    runBlocking {
      if (usersRepository.update(newUser)) {
        usersCache.invalidate(oldUser.id)
        result.complete()
      } else throw HttpStatus.serverError()
    }

    return result
  }

  @Post("/:userId/change-role/:newRole")
  @RolesAllowed(Roles.Admin)
  fun changeRole(@PathParam("userId") userId: UUID, @PathParam("newRole") newRole: String): Promise<Unit> {
    val result = Promise.promise<Unit>()

    runBlocking {
      if (usersRepository.updateRole(userId, newRole)) {
        usersCache.invalidate(userId)
        result.complete()
      } else throw HttpStatus.serverError()
    }

    return result
  }

  @DELETE
  @RolesAllowed(Roles.User, Roles.Admin)
  fun delete(@Context user: User) {
    log.info { "deleting user: ${user.id}" }
  }
}
