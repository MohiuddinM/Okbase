package dev.muha.okbase

import com.zandero.rest.RestRouter
import dev.muha.okbase.controllers.*
import dev.muha.okbase.models.Document
import dev.muha.okbase.models.Permission
import dev.muha.okbase.models.User
import dev.muha.okbase.readers.DocumentReader
import dev.muha.okbase.readers.PermissionReader
import dev.muha.okbase.readers.UserReader
import dev.muha.okbase.utils.Db
import dev.muha.okbase.utils.GenericExceptionHandler
import dev.muha.okbase.utils.HttpStatusExceptionHandler
import dev.muha.okbase.utils.JwtAuth
import dev.muha.okbase.writers.DocumentWriter
import dev.muha.okbase.writers.ListWriter
import dev.muha.okbase.writers.PermissionWriter
import dev.muha.okbase.writers.UserWriter
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import mu.KotlinLogging
import org.slf4j.bridge.SLF4JBridgeHandler

fun main() {
  SLF4JBridgeHandler.install()
  System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO")
  System.setProperty(org.slf4j.simple.SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, "true")
  System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory")

  val options = DeploymentOptions().setInstances(1)
  Vertx.vertx().deployVerticle("dev.muha.okbase.MainVerticle", options)
}

class MainVerticle : AbstractVerticle() {
  private val log = KotlinLogging.logger {}

  override fun start(startFuture: Future<Void>) {

    Db.initDb()

    val router = Router.router(vertx)
    router.route().handler(JwtAuth.handler())

    RestRouter.register(
      router,
      ServerStatusController::class.java,
      UsersController::class.java,
      AuthenticationsController::class.java,
      SyncController::class.java,
      PermissionsController::class.java,
      DocumentsController::class.java,
      CollectionsController::class.java,
      VerificationsController::class.java
    )

    RestRouter.getReaders().register(User::class.java, UserReader::class.java)
    RestRouter.getReaders().register(Document::class.java, DocumentReader::class.java)
    RestRouter.getReaders().register(Permission::class.java, PermissionReader::class.java)

    RestRouter.getWriters().register(User::class.java, UserWriter::class.java)
    RestRouter.getWriters().register(Document::class.java, DocumentWriter::class.java)
    RestRouter.getWriters().register(Permission::class.java, PermissionWriter::class.java)
    RestRouter.getWriters().register(List::class.java, ListWriter::class.java)

    RestRouter.getExceptionHandlers()
      .register(HttpStatusExceptionHandler::class.java, GenericExceptionHandler::class.java)

    vertx.createHttpServer().requestHandler(router).listen(Config.appPort)

    println("Started listening on localhost:${Config.appPort}")
  }
}
