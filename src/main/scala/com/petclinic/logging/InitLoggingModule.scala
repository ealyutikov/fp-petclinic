package com.petclinic.logging

import cats.effect.Sync
import com.petclinic.logging.Logger.Log
import distage.{ModuleDef, TagK}
import izumi.logstage.api.logger.AbstractLogger
import logstage.IzLogger

final class InitLoggingModule[I[_] : TagK] extends ModuleDef {
  make[IzLogger].fromResource[Logger.Maker[I]].aliased[AbstractLogger]
  make[Log[I]].fromEffect { (izLogger: IzLogger, F: Sync[I]) =>
    implicit val I: Sync[I] = F
    Log.fromLogger[I](izLogger)
  }
}
