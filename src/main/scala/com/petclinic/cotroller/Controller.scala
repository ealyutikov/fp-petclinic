package com.petclinic.cotroller

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.{catsSyntaxSemigroup, toSemigroupKOps}
import cats.kernel.Semigroup
import com.petclinic.error.HttpError
import com.petclinic.error.HttpError._
import com.petclinic.model.ExpectedHeaders
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

trait Controller[I[_]] {
  def routes: HttpRoutes[I]
  def endpoints: NonEmptyList[Endpoint[_, _, _, _]]
}

object Controller {

  val baseEndpoint = endpoint
    .in("api")
    .in(ExpectedHeaders.endpointInput)
    .errorOut(
      oneOf[HttpError](
        oneOfMapping(StatusCode.NotFound, jsonBody[NotFound].description("not found")),
        oneOfMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
        oneOfMapping(StatusCode.InternalServerError, jsonBody[Internal].description("internal error")),
        oneOfMapping(StatusCode.NoContent, emptyOutputAs(NoContent))
        //oneOfDefaultMapping(jsonBody[Unknown].description("unknown"))
      )
    )

  implicit def catsSemigroupForRoutes[I[_] : Monad]: Semigroup[Controller[I]] =
    (x, y) =>
      new Controller[I] {
        val routes: HttpRoutes[I] = x.routes <+> y.routes
        val endpoints: NonEmptyList[Endpoint[_, _, _, _]] = x.endpoints |+| y.endpoints
      }

}
