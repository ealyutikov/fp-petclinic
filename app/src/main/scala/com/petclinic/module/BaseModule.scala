package com.petclinic.module

import cats.effect._
import cats.{Applicative, Functor, Monad}
import com.petclinic.util.aliases.Trans
import distage.{ModuleDef, TagK}
import tofu.{ApplicativeThrow, MonadThrow}

final class BaseModule[F[_] : ConcurrentEffect : ContextShift : Timer : TagK, DB[_] : Trans : TagK] extends ModuleDef {

  addImplicit[ContextShift[F]]

  addImplicit[ConcurrentEffect[F]]
    .aliased[ApplicativeThrow[F]]
    .aliased[Bracket[F, Throwable]]
    .aliased[MonadThrow[F]]
    .aliased[Concurrent[F]]
    .aliased[Effect[F]]
    .aliased[Async[F]]
    .aliased[Sync[F]]

  addImplicit[Monad[F]]
    .aliased[Applicative[F]]
    .aliased[Functor[F]]

  addImplicit[Timer[F]]

  addImplicit[Trans[DB]]

}
