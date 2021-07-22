package com.petclinic.cotroller

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.{catsSyntaxSemigroup, toSemigroupKOps}
import cats.kernel.Semigroup
import com.petclinic.model.ExpectedHeaders
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import org.http4s.HttpRoutes
import sttp.tapir.{endpoint, Endpoint}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

trait Controller[I[_]] {
  def routes: HttpRoutes[I]
  def endpoints: NonEmptyList[Endpoint[_, _, _, _]]
}

object Controller {

  val baseEndpoint = endpoint
    .in("api")
    .in(ExpectedHeaders.endpointInput)
    .errorOut(jsonBody[ErrorResponse])

  @derive(encoder, decoder)
  final case class ErrorResponse(error: String)

  implicit def catsSemigroupForRoutes[I[_] : Monad]: Semigroup[Controller[I]] =
    (x, y) =>
      new Controller[I] {
        val routes: HttpRoutes[I] = x.routes <+> y.routes
        val endpoints: NonEmptyList[Endpoint[_, _, _, _]] = x.endpoints |+| y.endpoints
      }
}
