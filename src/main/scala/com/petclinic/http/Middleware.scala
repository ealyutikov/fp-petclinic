package com.petclinic.http

import cats.Endo
import cats.data.Kleisli
import cats.syntax.semigroupk._
import cats.effect.{Clock, Concurrent, Resource, Sync}
import com.petclinic.logging.Logger._
import io.chrisdavenport.epimetheus.CollectorRegistry
import io.chrisdavenport.epimetheus.CollectorRegistry.Unsafe
import org.http4s.HttpRoutes
import org.http4s.metrics.prometheus.{Prometheus, PrometheusExportService}
import org.http4s.server.middleware.{Metrics, RequestLogger, ResponseLogger}

object Middleware {

  private final val excludePaths = Set.empty[String]

  def metered[F[_] : Sync : Clock](cr: CollectorRegistry[F], name: String): Resource[F, Endo[HttpRoutes[F]]] = {
    val jcr = Unsafe.asJava(cr)
    for {
      metricsOps <- Prometheus.metricsOps[F](jcr, name)
    } yield (routes: HttpRoutes[F]) => PrometheusExportService.service[F](jcr) <+> Metrics[F](metricsOps)(routes)
  }

  def logged[F[_] : Concurrent : Log]: Endo[HttpRoutes[F]] = routes =>
    Kleisli { request =>
      if (excludePaths.exists(request.uri.path.renderString.contains(_))) routes.run(request)
      else loggedRequest.andThen(loggedResponse)(routes).run(request)
    }

  private def loggedResponse[F[_] : Concurrent : Log]: Endo[HttpRoutes[F]] =
    ResponseLogger
      .httpRoutes(logHeaders = true, logBody = false, logAction = Some(log.raw.info(_)))(_)

  private def loggedRequest[F[_] : Concurrent : Log]: Endo[HttpRoutes[F]] =
    RequestLogger
      .httpRoutes(logHeaders = true, logBody = false, logAction = Some(log.raw.info(_)))(_)

}
