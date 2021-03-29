package com.petclinic.config

import cats.effect.{Blocker, ContextShift, Sync}
import com.petclinic.config.AppConfig._
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

import pureconfig.generic.auto._

final case class AppConfig(http: HttpConfig, db: DbConfig, flyway: FlywayConfig, log: LogConfig)

object AppConfig {

  def load[F[_] : Sync : ContextShift](blocker: Blocker): F[AppConfig] =
    ConfigSource.default.at("app").loadF(blocker)

  final case class FlywayConfig(location: String, enableMigrations: Boolean = false)

  final case class HttpConfig(host: String, port: Int)

  final case class DbConfig(driverClassName: String, url: String, user: String, pass: String, poolSize: Int)

}
