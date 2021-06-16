package com.petclinic.cotroller

import cats.data.NonEmptyList
import cats.effect.{Concurrent, ContextShift, Timer}
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.functor._
import com.petclinic.context.AppCtx
import com.petclinic.cotroller.Controller.ErrorResponse
import com.petclinic.model.{ExpectedHeaders, Vet}
import com.petclinic.service.VetService
import izumi.distage.model.definition.Lifecycle
import org.http4s.HttpRoutes
import sttp.tapir.{endpoint, _}
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tofu.WithRun

final case class VetController[I[_] : Concurrent: ContextShift: Timer, F[_]](
    service: VetService[F]
)(implicit WR: WithRun[F, I, AppCtx])
    extends Controller[I] {

  private val vetEndpoint = endpoint.get
    .in("api" / "vet")
    .in(ExpectedHeaders.endpointInput)
    .out(jsonBody[List[Vet]])
    .errorOut(jsonBody[ErrorResponse])

  override def routes: HttpRoutes[I] =
    Http4sServerInterpreter[I]()
      .toRoutes(vetEndpoint) { headers =>
        val ctx = AppCtx(headers.requestId, headers.sessionId)
        WR.runContext(service.findAll())(ctx).attempt.map(_.leftMap(error => ErrorResponse(error.getMessage)))
      }

  def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(vetEndpoint)

}

object VetController {

  final class Maker[I[_]: Concurrent: ContextShift: Timer, F[_]](service: VetService[F])(implicit
      WR: WithRun[F, I, AppCtx]
  ) extends Lifecycle.Of(Lifecycle.pure(VetController[I, F](service)))

}
