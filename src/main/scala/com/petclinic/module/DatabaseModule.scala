package com.petclinic.module

import com.petclinic.database.{DoobieLogHandler, Migrator, Transactor}
import com.petclinic.util.aliases.Trans
import distage.TagK
import doobie.LogHandler
import izumi.distage.model.definition.ModuleDef

final class DatabaseModule[F[_] : TagK] extends ModuleDef {

  make[Migrator[F]].fromResource[Migrator.Maker[F]]
  make[Trans[F]].fromResource[Transactor.Maker[F]]
  make[LogHandler].from(DoobieLogHandler.make())

}
