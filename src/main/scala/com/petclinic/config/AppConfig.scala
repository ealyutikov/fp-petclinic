package com.petclinic.config

import com.petclinic.config.AppConfig.{Http, Swagger}
import derevo.derive
import derevo.pureconfig.config
import pureconfig.ConfigReader
import sttp.tapir.openapi.Server
import scala.collection.immutable.ListMap
import pureconfig.generic.auto._

@derive(config)
final case class AppConfig(http: Http, swagger: Swagger, dbConfig: DbConfig, logger: LogConfig, flyway: FlywayConfig)

object AppConfig extends WithHints {

  implicit def listMapConfigReaderDerived[V : ConfigReader]: ConfigReader[ListMap[String, V]] =
    ConfigReader.mapReader[V].map(m => ListMap(m.toSeq: _*))

  @derive(config)
  final case class Http(host: String, port: Int)

  @derive(config)
  final case class Swagger(enabled: Boolean, servers: List[Server])

}
