package com.petclinic.logging

import cats.effect.Sync
import com.petclinic.logging.Logger.Log
import distage.{Id, ModuleDef, TagK}
import izumi.logstage.api.logger.AbstractLogger
import logstage.IzLogger

final class InitLoggingModule[I[_] : TagK] extends ModuleDef {
  make[IzLogger].named("application").fromResource[Logger.Maker[I]].aliased[AbstractLogger]
  make[Log[I]].from { (izLogger: IzLogger @Id("application"), sync: Sync[I]) =>
    implicit val I: Sync[I] = sync
    Log.fromLogger[I](izLogger)
  }
}
