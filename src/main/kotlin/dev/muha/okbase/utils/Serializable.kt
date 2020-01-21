package dev.muha.okbase.utils

import io.vertx.core.json.JsonObject

interface Serializable {
  fun toJson(): JsonObject
}
