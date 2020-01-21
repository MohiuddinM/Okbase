package dev.muha.okbase.utils

object Roles {
  const val Anonymous = "Anonymous"               // A user whose role is not known (e.g. Signed Out user)
  const val User = "User"                         // Normal User (Standard)
  const val Admin = "Admin"                       // Admin which has read, write and manage right to everything (e.g. Moderator)
  const val ReadOnlyAdmin = "ReadOnlyAdmin"       // Admin which only has read access to everything (e.g. Analytics)
}
