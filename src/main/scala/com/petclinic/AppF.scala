package com.petclinic

import cats.Parallel
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.syntax.apply._
import com.petclinic.ctx.LocalCtx
import com.petclinic.database.Migrator
import com.petclinic.logger.Logger.Log
import com.petclinic.module._
import distage.{Injector, TagK}
import izumi.distage.model.plan.Roots
import org.http4s.server.blaze.BlazeServerBuilder

trait AppF {

  def runF[F[_] : ConcurrentEffect : ContextShift : Timer : LocalCtx : TagK : Parallel]: F[Unit] = {

    val finalModule = Seq(
      new BaseModule[F],
      new RepositoryModule[F],
      new ControllersModule[F],
      new LoggingModule[F],
      new ServerModule[F],
      new ServiceModule[F],
      new ConfigModule[F],
      new DatabaseModule[F]
    ).merge

    val injector = Injector[F]()
    val plan = injector.plan(finalModule, Roots.Everything)
    val resource = injector.produce(plan)
    resource
      .use { locator =>
        locator.get[Migrator[F]].migrate *>
        locator.get[Log[F]].info("*** SERVICE STARTED ***") *>
        locator.get[BlazeServerBuilder[F]].serve.compile.drain
      }
  }

}
