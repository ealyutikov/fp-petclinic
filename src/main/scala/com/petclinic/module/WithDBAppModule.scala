package com.petclinic.module

import cats.{Defer, Monad}
import cats.effect.Sync
import com.petclinic.logging.CtxLoggingModule
import com.petclinic.model.AppCtx
import com.petclinic.repository.RepositoryModule
import com.petclinic.service.ServiceModule
import distage.{ModuleDef, TagK}
import doobie.ConnectionIO
import tofu._
import tofu.doobie.LiftConnectionIO
import tofu.lift.Lift

final class WithDBAppModule[
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

  include(new RepositoryModule[DB])
  include(new ServiceModule[F, DB])
  include(new CtxLoggingModule[F, DB])

}
