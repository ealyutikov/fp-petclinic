package com.petclinic.module

import cats.{Defer, Monad}
import cats.effect.Sync
import com.petclinic.context.AppCtx
import com.petclinic.database.DoobieLogHandler
import com.petclinic.repository.RepositoryModule
import com.petclinic.service.ServiceModule
import distage.{ModuleDef, TagK}
import doobie.ConnectionIO
import tofu._
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.lift.Lift

final class WithDBModule[
  I[_] : TagK,
  F[_] : TagK,
  DB[_] : Sync : WithRun[*[_], ConnectionIO, AppCtx] : Lift[I, *[_]] : Lift[F, *[_]] : TagK
] extends ModuleDef {

  addImplicit[Sync[DB]]
    .aliased[Defer[DB]]
    .aliased[BracketThrow[DB]]
    .aliased[Monad[DB]]

  addImplicit[Lift[I, DB]]
  addImplicit[Lift[F, DB]]
  addImplicit[LiftConnectionIO[DB]]

  addImplicit[WithRun[DB, ConnectionIO, AppCtx]]
    .aliased[WithLocal[DB, AppCtx]]

  make[EmbeddableLogHandler[DB]].fromResource[DoobieLogHandler.Maker[F, DB]]

  include(new RepositoryModule[DB])
  include(new ServiceModule[F, DB])

}
