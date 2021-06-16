package com.petclinic.cotroller

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.{catsSyntaxSemigroup, toSemigroupKOps}
import cats.kernel.Semigroup
import io.circe.generic.JsonCodec
import org.http4s.HttpRoutes
import sttp.tapir.Endpoint

trait Controller[I[_]] {
  def routes: HttpRoutes[I]
  def endpoints: NonEmptyList[Endpoint[_, _, _, _]]
}

object Controller {

  @JsonCodec
  final case class ErrorResponse(error: String)

  implicit def catsSemigroupForRoutes[I[_] : Monad]: Semigroup[Controller[I]] =
    (x, y) =>
      new Controller[I] {
        val routes: HttpRoutes[I] = x.routes <+> y.routes
        val endpoints: NonEmptyList[Endpoint[_, _, _, _]] = x.endpoints |+| y.endpoints
      }
}
