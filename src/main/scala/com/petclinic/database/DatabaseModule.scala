package com.petclinic.database

import cats.Monad
import cats.effect.Sync
import com.petclinic.database.DatabaseModule.TxrMaker
import com.petclinic.model.AppCtx
import distage.{Lifecycle, TagK}
import doobie.Transactor
import izumi.distage.model.definition.ModuleDef
import tofu.WithRun
import tofu.doobie.transactor.Txr

final class DatabaseModule[I[_] : TagK, F[_] : TagK] extends ModuleDef {
  make[DBMigrator[I]].fromResource[DBMigrator.Maker[I]]
  make[Transactor[I]].fromResource[DBTransactor.Maker[I]]
  make[Txr.Contextual[F, AppCtx]].fromResource[TxrMaker[I, F]]
}

object DatabaseModule {
  final class TxrMaker[I[_] : Sync, F[_] : Monad : WithRun[*[_], I, AppCtx]](t: Transactor[I])
    extends Lifecycle.Of(Lifecycle.pure(Txr.contextual[F][I, AppCtx](t)))
}
