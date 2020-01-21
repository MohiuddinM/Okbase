package dev.muha.okbase.utils

import com.zaxxer.hikari.HikariDataSource
import dev.muha.okbase.Config
import dev.muha.okbase.sql.Documents
import dev.muha.okbase.sql.Permissions
import dev.muha.okbase.sql.Users
import io.vertx.core.Vertx
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Db {
  private val log = KotlinLogging.logger {}

  private val vertx = Vertx.currentContext().owner()

  fun initDb() {
    val ds = HikariDataSource()
    ds.jdbcUrl = "jdbc:${Config.postgresConnection}://${Config.postgresHost}:${Config.postgresPort}/${Config.postgresDbName}"
    ds.username = Config.postgresUser
    ds.password = Config.postgresPassword

    Database.connect(ds)

    transaction {
      SchemaUtils.create(Users, Permissions, Documents)
    }
  }
}
