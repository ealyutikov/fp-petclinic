package com.petclinic

import cats.effect._
import com.petclinic.module.{BaseModule, ControllersModule, RepositoryModule}
import distage.Injector
import doobie.ConnectionIO
import izumi.distage.model.plan.Roots
import com.petclinic.util.aliases.idLift
import org.http4s.server.blaze.BlazeServerBuilder

object Application extends IOApp {

  type F[A] = IO[A]
  type DB[A] = ConnectionIO[A]

  override def run(args: List[String]): F[ExitCode] = {
    val finalModule = Seq(
      new BaseModule[F, DB],
      new RepositoryModule[DB],
      new ControllersModule[F]
    ).merge

    val injector = Injector()
    val plan = injector.plan(finalModule, Roots.Everything)
    val resource = injector.produceF[F](plan)
    resource
      .use { locator =>
        locator.get[BlazeServerBuilder[F]].serve.compile.lastOrError
      } as ExitCode.Success
  }

}
