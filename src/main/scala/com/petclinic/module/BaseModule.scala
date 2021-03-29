package com.petclinic.module

import cats.{Applicative, Functor, Monad}
import cats.effect._
import com.petclinic.ctx.{AskCtx, LocalCtx}
import com.petclinic.logger.Logger
import com.petclinic.logger.Logger.LogCtx
import com.petclinic.util.ec
import distage.{ModuleDef, TagK}
import tofu.{ApplicativeThrow, MonadThrow, WithContext}
import tofu.generate.GenUUID

final class BaseModule[F[_] : ConcurrentEffect : ContextShift : Timer : TagK : LocalCtx] extends ModuleDef {

  addImplicit[ContextShift[F]]

  addImplicit[ConcurrentEffect[F]]
    .aliased[ApplicativeThrow[F]]
    .aliased[BracketThrow[F]]
    .aliased[MonadThrow[F]]
    .aliased[Concurrent[F]]
    .aliased[Effect[F]]
    .aliased[Async[F]]
    .aliased[Sync[F]]

  addImplicit[Monad[F]]
    .aliased[Applicative[F]]
    .aliased[Functor[F]]

  addImplicit[Timer[F]]

  addImplicit[LocalCtx[F]]
    .aliased[AskCtx[F]]

  make[Blocker].fromResource(ec.blocker[F]("app-blocker"))

  make[GenUUID[F]].from(GenUUID.syncGenUUID[F])

  make[WithContext[F, LogCtx]].from { (L: LocalCtx[F]) =>
    new WithContext[F, LogCtx] {
      def functor: Functor[F] = L.functor
      def context: F[LogCtx] = L.functor.map(L.context)(Logger.mapToLogCtx(_))
    }
  }

}
