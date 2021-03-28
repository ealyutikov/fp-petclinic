package com.petclinic.module

import com.petclinic.logger.{CtxLog, Logger}
import com.petclinic.logger.Logger.Log
import distage.{ModuleDef, TagK}
import izumi.logstage.api.logger.AbstractLogger
import logstage.IzLogger

final class LoggingModule[F[_] : TagK] extends ModuleDef {

  make[IzLogger]
    .fromResource[Logger.Maker[F]]
    .aliased[AbstractLogger]

  make[Log[F]].fromResource[CtxLog.Maker[F]]

}
