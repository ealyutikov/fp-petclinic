package com.petclinic.module

import cats.effect.Sync
import com.petclinic.config.AppConfig
import com.petclinic.logger.{CtxLogger, Logger}
import distage.{ModuleDef, TagK}
import izumi.logstage.api.logger.AbstractLogger
import logstage.{IzLogger, LogIO}

final class LoggerModule[F[_] : TagK] extends ModuleDef {

  make[IzLogger].fromEffect(Logger.make[F](_: AppConfig.LoggerConfig)(_: Sync[F]))
  make[AbstractLogger].using[IzLogger]

  make[LogIO[F]].fromResource[CtxLogger.Maker[F]]

}
