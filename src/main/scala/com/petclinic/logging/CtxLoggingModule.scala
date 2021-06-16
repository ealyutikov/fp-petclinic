package com.petclinic.logging

import com.petclinic.logging.Logger.Log
import distage.{ModuleDef, TagK}

final class CtxLoggingModule[F[_] : TagK] extends ModuleDef {
  make[Log[F]].fromResource[CtxLog.Maker[F]]
}
