package com.petclinic.config

import cats.effect.{Blocker, ContextShift, Sync}
import com.petclinic.config.AppConfig._
import pureconfig.{ConfigSource, _}
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource
import pureconfig.generic.auto._
import scala.collection.immutable.ListMap

final case class AppConfig(http: Http, dbConfig: DbConfig, flyway: FlywayConfig, logConfig: LogConfig)

object AppConfig {

  def load[F[_] : Sync : ContextShift](blocker: Blocker): F[AppConfig] =
    ConfigSource.default.at("app").loadF(blocker)

  implicit def listMapConfigReaderDerived[V : ConfigReader]: ConfigReader[ListMap[String, V]] =
    ConfigReader.mapReader[V].map(m => ListMap(m.toSeq: _*))

  final case class FlywayConfig(location: String, enableMigrations: Boolean = false)

  final case class Http(host: String, port: Int)

  final case class DbConfig(driverClassName: String, url: String, user: String, pass: String, poolSize: Int)

}
