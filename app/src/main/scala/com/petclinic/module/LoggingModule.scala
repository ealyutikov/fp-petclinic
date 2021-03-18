package com.petclinic.module

import com.petclinic.logger.CtxLog
import distage.{ModuleDef, TagK}
import logstage.strict.LogIOStrict

final class LoggingModule[F[_]: TagK] extends ModuleDef {

  make[LogIOStrict[F]].fromResource[CtxLog.Maker[F]]

}
