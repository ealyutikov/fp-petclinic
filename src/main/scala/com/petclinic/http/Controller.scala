package com.petclinic.http

import cats.Monad
import cats.implicits.toSemigroupKOps
import cats.kernel.Semigroup
import io.circe.generic.JsonCodec
import org.http4s.HttpRoutes

trait Controller[F[_]] {
  def routes: HttpRoutes[F]
}

object Controller {

  @JsonCodec
  final case class ErrorResponse(error: String)

  implicit def catsSemigroupForRoutes[F[_] : Monad]: Semigroup[Controller[F]] =
    (x, y) =>
      new Controller[F] {
        val routes: HttpRoutes[F] = x.routes <+> y.routes
      }

}
