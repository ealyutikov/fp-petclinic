package com.petclinic

import cats.effect._
import cats.syntax.apply._
import com.petclinic.ctx._
import com.petclinic.module._
import distage.Injector
import izumi.distage.model.plan.Roots
import logstage.LogIO
import org.http4s.server.blaze.BlazeServerBuilder

object Application extends IOApp {

  type F[A] = Env[IO, A]

  override def run(args: List[String]): IO[ExitCode] = {
    val finalModule = Seq(
      new BaseModule[F],
      new RepositoryModule[F],
      new ControllersModule[F],
      new LoggingModule[F],
      new ServerModule[F]
    ).merge

    val injector = Injector[F]()
    val plan = injector.plan(finalModule, Roots.Everything)
    val resource = injector.produce(plan)
    resource
      .use { locator =>
        locator.get[LogIO[F]].info("*** SERVICE STARTED ***") *>
        locator.get[BlazeServerBuilder[F]].serve.compile.lastOrError
      }
      .run(EmptyCtx)
  }

}
