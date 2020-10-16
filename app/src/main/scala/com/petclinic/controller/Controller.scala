package com.petclinic.controller

import java.util.UUID

import cats.Monad
import cats.data.NonEmptyList
import cats.implicits.{catsSyntaxSemigroup, toSemigroupKOps}
import cats.kernel.Semigroup
import io.circe.generic.JsonCodec
import org.http4s.HttpRoutes
import sttp.tapir.Endpoint

trait Controller[F[_]] {

  def routes: HttpRoutes[F]
  def endpoints: NonEmptyList[Endpoint[_, _, _, _]]

}

object Controller {
  implicit def catsSemigroupForRoutes[F[_] : Monad]: Semigroup[Controller[F]] =
    (x, y) =>
      new Controller[F] {
        val routes: HttpRoutes[F] = x.routes <+> y.routes
        val endpoints: NonEmptyList[Endpoint[_, _, _, _]] = x.endpoints |+| y.endpoints
      }

  @JsonCodec
  final case class EmptyResponse()

  @JsonCodec
  final case class TraceId(value: UUID) extends AnyVal

  @JsonCodec
  final case class AppVersion(value: String)

  @JsonCodec
  final case class BuildVersion(number: Int, branch: String, commit: String, time: String)

  @JsonCodec
  final case class BuildInfoResponse(name: String, version: AppVersion, build: BuildVersion)

}
