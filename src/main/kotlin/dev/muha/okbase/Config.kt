package dev.muha.okbase

object Config {
  const val appHost = "localhost"
  const val appPort = 8888
  const val salt = "lIjoiVGVzdCBVc2Vy"

  const val jwtSecretKey = "Li4c6xWMsT9Kj6o3Oh9Q2"

  const val postgresConnection = "postgresql"
  const val postgresHost = "localhost"
  const val postgresPort = "5432"
  const val postgresDbName = "okbase"
  const val postgresUser = "postgres"
  const val postgresPassword = "root"

  // val documentAllowedTypes = listOf<String>("*")

  const val smtpHost = "smtp.mailtrap.io"
  const val smtpPort = 2525
  const val smtpUsername = "937ae987fe74f9"
  const val smtpPassword = "d3753d6a48b552"

  const val mongoDbName = "backend-db"


  const val redisHost = "127.0.0.1"
  const val redisPassword = ""
  const val redisPort = 6379


  const val jwtValidity = 86400
  const val jwtIssuer = "com.mohiuddin.okbase"
}

