package com.petclinic

import cats.effect._
import cats.syntax.apply._
import com.petclinic.ctx._
import com.petclinic.database.Migrator
import com.petclinic.logger.Logger.Log
import com.petclinic.module._
import distage.Injector
import izumi.distage.model.plan.Roots
import org.http4s.server.blaze.BlazeServerBuilder

object Application extends IOApp {

  type F[A] = Env[IO, A]

  override def run(args: List[String]): IO[ExitCode] = {
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
        locator.get[BlazeServerBuilder[F]].serve.compile.lastOrError
      }
      .run(EmptyCtx)
  }

}
