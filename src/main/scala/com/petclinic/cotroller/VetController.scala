package com.petclinic.cotroller

import cats.data.NonEmptyList
import cats.effect.{Concurrent, ContextShift, Timer}
import com.petclinic.cotroller.Controller.baseEndpoint
import com.petclinic.model.{AppCtx, Vet}
import com.petclinic.service.VetService
import com.petclinic.util.error._
import izumi.distage.model.definition.Lifecycle
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import tofu.WithRun

final case class VetController[I[_] : Concurrent : ContextShift : Timer, F[_]](
  service: VetService[F]
)(implicit WR: WithRun[F, I, AppCtx])
  extends Controller[I] {

  private val vetEndpoint = baseEndpoint
    .get
    .in("vet")
    .out(jsonBody[List[Vet]])

  override def routes: HttpRoutes[I] =
    Http4sServerInterpreter[I]()
      .toRoutes(vetEndpoint) { headers =>
        val ctx = AppCtx(headers.requestId, headers.sessionId)
        WR.runContext(service.findAll())(ctx).toError
      }

  def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(vetEndpoint)

}

object VetController {

  final class Maker[I[_] : Concurrent : ContextShift : Timer, F[_]](service: VetService[F])(implicit
    WR: WithRun[F, I, AppCtx]
  ) extends Lifecycle.Of(Lifecycle.pure(VetController[I, F](service)))

}
