package com.petclinic.config

import com.petclinic.config.AppConfig._
import distage.{ModuleDef, TagK}

final class ConfigModule[I[_] : TagK] extends ModuleDef {
  make[ConfigLoader[I]].from[ConfigLoader.Impl[I]]
  make[AppConfig].fromEffect((_: ConfigLoader[I]).loadConfig("app"))
  make[Http].from((_: AppConfig).http)
  make[DbConfig].from((_: AppConfig).dbConfig)
  make[FlywayConfig].from((_: AppConfig).flyway)
  make[LogConfig].from((_: AppConfig).logger)
  make[Swagger].from((_: AppConfig).swagger)
}
