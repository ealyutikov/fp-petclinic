package com.petclinic.database

import cats.Monad
import cats.effect.Sync
import com.petclinic.context.AppCtx
import com.petclinic.database.DatabaseModule.TxrMaker
import distage.{Lifecycle, TagK}
import doobie.Transactor
import izumi.distage.model.definition.ModuleDef
import tofu.WithRun
import tofu.doobie.transactor.Txr

final class DatabaseModule[I[_] : TagK, F[_] : TagK] extends ModuleDef {
  make[Flyway[I]].fromResource[Flyway.Maker[I]]
  make[Transactor[I]].fromResource[DoobieTransactor.Maker[I]]
  make[Txr.Contextual[F, AppCtx]].fromResource[TxrMaker[I, F]]
}

object DatabaseModule {
  final class TxrMaker[I[_] : Sync, F[_] : Monad : WithRun[*[_], I, AppCtx]](t: Transactor[I])
    extends Lifecycle.Of(Lifecycle.pure(Txr.contextual[F][I, AppCtx](t)))
}
