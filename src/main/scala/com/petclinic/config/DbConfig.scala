package com.petclinic.config

import derevo.derive
import derevo.pureconfig.config

import scala.concurrent.duration.FiniteDuration

@derive(config)
final case class DbConfig(connection: DbConnection, driverClassName: String, hikari: Option[Hikari], warmUp: Boolean)

object DbConfig extends WithHints

@derive(config)
final case class Hikari(
  connectionTimeout: Option[FiniteDuration],
  connectionInitSql: Option[String],
  maximumPoolSize: Option[Int],
  maxLifetime: Option[FiniteDuration]
)

object Hikari extends WithHints

@derive(config)
final case class DbConnection(url: String, user: String, pass: String)
