package com.petclinic

import cats.Parallel
import cats.effect.{Concurrent, ConcurrentEffect, ContextShift, Sync, Timer}
import cats.syntax.apply._
import com.petclinic.database.{DBMigrator, DBWarmer}
import com.petclinic.http.HttpServer
import com.petclinic.logging.Logger.Log
import com.petclinic.model.AppCtx
import com.petclinic.module.{InitModule, WithDBAppModule, WithFAppModule}
import distage.{Injector, ModuleDef, TagK}
import doobie.ConnectionIO
import izumi.distage.model.plan.Roots
import tofu.{Execute, WithRun}
import tofu.lift.{Lift, UnliftIO}

trait AppF {

  def runF[
    I[_] : ConcurrentEffect : ContextShift : Timer : Execute : Parallel : TagK,
    F[_] : Concurrent : ContextShift : Timer : UnliftIO : WithRun[*[_], I, AppCtx] : TagK,
    DB[_] : Sync : WithRun[*[_], ConnectionIO, AppCtx] : Lift[I, *[_]] : Lift[F, *[_]] : TagK
  ]: I[Unit] = {

    val module = new ModuleDef {
      include(new InitModule[I])
      include(new WithFAppModule[I, F])
      include(new WithDBAppModule[I, F, DB])
    }

    val injector = Injector[I]()
    val plan = injector.plan(module, Roots.Everything)
    val resource = injector.produce(plan)
    resource
      .use { locator =>
        locator.get[DBWarmer[I]].warmUp *>
        locator.get[DBMigrator[I]].migrate *>
        locator.get[Log[I]].info("*** SERVICE STARTED ***") *>
        locator.get[HttpServer[fs2.Stream[I, *]]].serve.compile.drain
      }
  }

}
