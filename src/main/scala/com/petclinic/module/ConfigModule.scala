package com.petclinic.module

import cats.effect.{Blocker, ContextShift, Sync}
import com.petclinic.config.{AppConfig, LogConfig}
import com.petclinic.config.AppConfig.{HttpConfig, _}
import distage.{ModuleDef, TagK}

final class ConfigModule[F[_] : TagK] extends ModuleDef {

  make[AppConfig].fromEffect(AppConfig.load[F](_: Blocker)(_: Sync[F], _: ContextShift[F]))
  make[HttpConfig].from((_: AppConfig).http)
  make[DbConfig].from((_: AppConfig).db)
  make[FlywayConfig].from((_: AppConfig).flyway)
  make[LogConfig].from((_: AppConfig).log)

}
