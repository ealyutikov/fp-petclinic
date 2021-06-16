package com.petclinic.http

import cats.Endo
import cats.data.Kleisli
import cats.effect.Concurrent
import com.petclinic.logging.Logger._
import org.http4s.HttpRoutes
import org.http4s.server.middleware.{RequestLogger, ResponseLogger}

object Middleware {

  private final val excludePaths = Set("/docs", "/version", "/swagger")

  def logged[F[_]: Concurrent: Log]: Endo[HttpRoutes[F]] = routes =>
    Kleisli { request =>
      if (excludePaths.exists(request.uri.path.renderString.contains(_))) routes.run(request)
      else loggedRequest.andThen(loggedResponse)(routes).run(request)
    }

  private def loggedResponse[F[_]: Concurrent: Log]: Endo[HttpRoutes[F]] =
    ResponseLogger
      .httpRoutes(logHeaders = true, logBody = false, logAction = Some(log.raw.info(_)))(_)

  private def loggedRequest[F[_]: Concurrent: Log]: Endo[HttpRoutes[F]] =
    RequestLogger
      .httpRoutes(logHeaders = true, logBody = false, logAction = Some(log.raw.info(_)))(_)

}
