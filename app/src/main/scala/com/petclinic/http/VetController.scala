package com.petclinic.http

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.functor._
import com.petclinic.http.Controller.ErrorResponse
import com.petclinic.model.Vet
import com.petclinic.service.VetService
import distage.Lifecycle
import org.http4s.HttpRoutes
import sttp.tapir.{endpoint, _}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter

final case class VetController[F[_] : ContextShift : Concurrent : Timer](service: VetService[F]) extends Controller[F] {

  private val vetEndpoint =
    endpoint
      .get
      .in("api" / "vet")
      .out(jsonBody[List[Vet]])
      .errorOut(jsonBody[ErrorResponse])

  override def routes: HttpRoutes[F] =
    Http4sServerInterpreter.toRoutes(vetEndpoint) { _ =>
      service.findAll().attempt.map(_.leftMap(e => ErrorResponse(e.getMessage)))
    }

}

object VetController {

  final class Maker[F[_] : ContextShift : Concurrent : Timer](service: VetService[F])
    extends Lifecycle.Of(Lifecycle.pure(VetController[F](service)))

}
