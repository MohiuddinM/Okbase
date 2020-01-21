package dev.muha.okbase.repos

import dev.muha.okbase.models.User
import dev.muha.okbase.sql.Users
import dev.muha.okbase.utils.Roles
import dev.muha.okbase.utils.toBcryptHash
import mu.KotlinLogging
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.*

class UsersRepository {
  private val log = KotlinLogging.logger {}


  fun getById(id: UUID): User? {
    return transaction {
      try {
        val row = Users.select { Users.id.eq(id) }.firstOrNull() ?: return@transaction null

        return@transaction User.fromRow(row)
      } catch (e: Exception) {
        log.error { "could not get user by id: $e" }
        return@transaction null
      }
    }
  }

  fun getByEmail(email: String): User? {
    return transaction {
      try {
        val row = Users.select { Users.email.eq(email) }.firstOrNull() ?: return@transaction null

        return@transaction User.fromRow(row)
      } catch (e: Exception) {
        log.error { "could not get user by id: $e" }
        return@transaction null
      }
    }
  }

  fun store(user: User): Boolean {
    return transaction {
      try {
        Users.insert {
          it[id] = user.id
          it[role] = Roles.User
          it[password] = user.password!!.toBcryptHash()
          it[email] = user.email
          it[createdAt] = LocalDateTime.now()
          it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction true
      } catch (e: Exception) {
        log.error { "could not store user: $e" }
        return@transaction false
      }
    }
  }

  fun update(user: User): Boolean {
    return transaction {
      try {
        Users.update({ Users.id.eq(user.id) }) {
          it[role] = user.role
          it[email] = user.email
          it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction true
      } catch (e: Exception) {
        log.error { "could not update user: $e" }
        return@transaction false
      }
    }
  }

  fun updatePasswordByEmail(email: String, password: String): Boolean {
    return transaction {
      try {
        Users.update({ Users.email.eq(email) }) {
          it[Users.password] = password
          it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction true
      } catch (e: Exception) {
        log.error { "could not update user: $e" }
        return@transaction false
      }
    }
  }

  fun updateRole(userId: UUID, newRole: String): Boolean {
    return transaction {
      try {
        Users.update({ Users.id.eq(userId) }) {
          it[role] = newRole
          it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction true
      } catch (e: Exception) {
        log.error { "could not update user's role: $e" }
        return@transaction false
      }
    }
  }

  fun setEmailVerified(email: String): Boolean {
    return transaction {
      try {
        Users.update({ Users.email.eq(email) }) {
          it[emailVerifiedAt] = LocalDateTime.now()
          it[modifiedAt] = LocalDateTime.now()
        }

        return@transaction true
      } catch (e: Exception) {
        log.error { "could not update user: $e" }
        return@transaction false
      }
    }
  }

  fun delete(id: UUID): Boolean {
    return transaction {
      try {
        Users.deleteWhere { Users.id.eq(id) }
        return@transaction true
      } catch (e: Exception) {
        log.error { "could not delete user: $e" }
        return@transaction false
      }
    }
  }
}
