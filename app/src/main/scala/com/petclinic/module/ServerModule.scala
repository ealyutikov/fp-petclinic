package com.petclinic.module

import cats.Monad
import cats.effect.{ConcurrentEffect, Timer}
import com.petclinic.config.AppConfig
import com.petclinic.controller.Controller
import com.petclinic.util.resources
import distage.{Id, ModuleDef, TagK}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

final class ServerModule[F[_] : TagK] extends ModuleDef {

  make[BlazeServerBuilder[F]].fromResource {
    (
      controllers: Controller[F] @Id("controllers"),
      httpConf: AppConfig.Http,
      ce: ConcurrentEffect[F],
      timer: Timer[F]
    ) =>
      implicit val M: Monad[F] = ce

      resources
        .cachedThreadPool[F](ce)
        .map { ec =>
          BlazeServerBuilder[F](ec)(ce, timer)
            .bindHttp(httpConf.port, httpConf.host)
            .withHttpApp(controllers.routes.orNotFound)
        }
  }

}
