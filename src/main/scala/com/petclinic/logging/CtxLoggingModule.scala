package com.petclinic.logging

import cats.tagless.syntax.functorK._
import com.petclinic.database.DBLogHandler
import com.petclinic.logging.Logger.Log
import com.petclinic.logging.implicits._
import distage.{ModuleDef, TagK}
import tofu.doobie.log.EmbeddableLogHandler
import tofu.lift.Lift

final class CtxLoggingModule[F[_] : TagK, DB[_] : TagK] extends ModuleDef {
  make[Log[F]].fromResource[CtxLog.Maker[F]]
  make[Log[DB]].from((fLog: Log[F], L: Lift[F, DB]) => fLog.mapK(L.liftF))
  make[EmbeddableLogHandler[DB]].fromResource[DBLogHandler.Maker[F, DB]]
}
