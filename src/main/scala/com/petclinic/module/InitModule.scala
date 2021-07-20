package com.petclinic.module

import cats.{Applicative, Apply, Functor, Monad}
import cats.effect._
import com.petclinic.config.ConfigModule
import com.petclinic.cotroller.ControllersModule
import com.petclinic.database.DBWarmer
import com.petclinic.http.ServerModule
import com.petclinic.logging.InitLoggingModule
import com.petclinic.metrics.MetricsModule
import com.petclinic.util.ec
import distage.{ModuleDef, TagK}
import tofu.{ApplicativeThrow => _, BracketThrow => _, MonadThrow => _, _}

final class InitModule[I[_] : ConcurrentEffect : ContextShift : Timer : Execute : TagK] extends ModuleDef {

  addImplicit[ContextShift[I]]

  addImplicit[Execute[I]]

  addImplicit[ConcurrentEffect[I]]
    .aliased[ApplicativeThrow[I]]
    .aliased[BracketThrow[I]]
    .aliased[MonadThrow[I]]
    .aliased[Concurrent[I]]
    .aliased[Effect[I]]
    .aliased[Async[I]]
    .aliased[Sync[I]]

  addImplicit[Monad[I]]
    .aliased[Applicative[I]]
    .aliased[Functor[I]]
    .aliased[Apply[I]]

  addImplicit[Timer[I]]
  addImplicit[Clock[I]]

  make[Blocker].fromResource(ec.blocker[I]("init-blocker")(_: Sync[I]))
  make[BlockExec[I]]
    .from(Scoped.blockerExecute(_: ContextShift[I], _: Blocker, _: Async[I]))
    .aliased[Blocks[I]]

  make[DBWarmer[I]].fromResource[DBWarmer.Maker[I]]

  include(new ConfigModule[I])
  include(new InitLoggingModule[I])
  include(new ServerModule[I])
  include(new ControllersModule[I])
  include(new MetricsModule[I])

}
