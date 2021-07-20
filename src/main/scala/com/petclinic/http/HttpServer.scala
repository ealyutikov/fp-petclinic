package com.petclinic.http

import cats.effect.{Async, ConcurrentEffect, Resource, Timer}
import cats.Endo
import cats.implicits.toSemigroupKOps
import cats.syntax.compose._
import com.petclinic.config.AppConfig
import com.petclinic.cotroller.{Controller, SwaggerController}
import com.petclinic.http.Middleware.{logged, metered}
import com.petclinic.logging.Logger.Log
import com.petclinic.util.resource._
import distage.{Id, Lifecycle}
import fs2.Stream
import io.chrisdavenport.epimetheus.CollectorRegistry
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import tofu.Execute

trait HttpServer[S[_]] {
  def serve: S[Unit]
}

object HttpServer {
  def never[I[_] : Async]: HttpServer[Stream[I, *]] =
    new HttpServer[Stream[I, *]] {
      def serve: Stream[I, Unit] = Stream.never[I]
    }

  final class Maker[I[_] : ConcurrentEffect : Timer : Execute : Log](
    controllers: Controller[I] @Id("controllers"),
    swaggerController: SwaggerController[I],
    config: AppConfig,
    collectorRegistry: CollectorRegistry[I]
  ) extends Lifecycle.OfCats(resource[I](controllers, swaggerController, config, collectorRegistry))

  private def resource[I[_] : ConcurrentEffect : Timer : Execute : Log](
    controllers: Controller[I],
    swaggerController: SwaggerController[I],
    config: AppConfig,
    collectorRegistry: CollectorRegistry[I]
  ): Resource[I, HttpServer[Stream[I, *]]] = {

    val swaggerMidl: Endo[HttpRoutes[I]] = routes => {
      if (config.swagger.enabled) swaggerController.routes <+> routes
      else routes
    }

    for {
      ec          <- Execute[I].executionContext.toResource
      meteredMidl <- metered(collectorRegistry, "http4s_server")
      midl = swaggerMidl >>> logged >>> meteredMidl
      _ <- BlazeServerBuilder[I](ec)
        .bindHttp(config.http.port, config.http.host)
        .withHttpApp(midl(controllers.routes).orNotFound)
        .withoutBanner
        .resource
    } yield never[I]
  }
}
