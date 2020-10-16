package com.petclinic.config

import cats.effect.{Blocker, ContextShift, Sync}
import com.petclinic.config.AppConfig._
import com.petclinic.config.AppConfig.LoggerConfig.Levels
import izumi.logstage.api.rendering.RenderingOptions
import mouse.all.anySyntaxMouse
import pureconfig.{CamelCase, ConfigFieldMapping, ConfigSource}
import pureconfig.generic.ProductHint
import pureconfig.module.catseffect.loadF
import sttp.tapir.openapi.Server
import pureconfig.generic.auto._
import pureconfig._

import scala.collection.immutable.ListMap
import scala.concurrent.duration.FiniteDuration

final case class AppConfig(http: Http, swagger: Swagger, dbConfig: DbConfig, flyway: FlywayConfig, logger: LoggerConfig)

object AppConfig {

  def load[F[_] : Sync : ContextShift](blocker: Blocker): F[AppConfig] = {
    implicit def hint[A]: ProductHint[A] = ProductHint(ConfigFieldMapping(CamelCase, CamelCase))
    ConfigSource.default.at("app") |> (loadF[F, AppConfig](_, blocker))
  }

  implicit def listMapConfigReaderDerived[V : ConfigReader]: ConfigReader[ListMap[String, V]] =
    ConfigReader.mapReader[V].map(m => ListMap(m.toSeq: _*))

  final case class FlywayConfig(location: String, enableMigrations: Boolean = false)

  final case class Http(host: String, port: Int)

  final case class Swagger(enabled: Boolean, servers: List[Server])

  final case class DbConfig(driverClassName: String, url: String, user: String, pass: String, poolSize: Int)

  final case class LoggerConfig(levels: Levels, options: Option[RenderingOptions], json: Boolean)
  object LoggerConfig {
    type Levels = Map[String, List[String]]
  }

}
