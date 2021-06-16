package com.petclinic.cotroller

import cats.data.NonEmptyList
import cats.effect.{Concurrent, ContextShift, Timer}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import com.petclinic.BuildInfo._
import com.petclinic.model.BuildInfoResponse.{AppVersion, BuildVersion}
import com.petclinic.model.BuildInfoResponse
import distage.Lifecycle
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

final case class BuildInfoController[I[_] : ContextShift : Concurrent : Timer]() extends Controller[I] {

  private val versionEndpoint = endpoint
    .get
    .in("version")
    .out(jsonBody[BuildInfoResponse])

  override def routes: HttpRoutes[I] =
    Http4sServerInterpreter[I]().toRoutes(versionEndpoint) { _ =>
      BuildInfoResponse(
        name,
        AppVersion(version),
        BuildVersion(buildNumber, buildBranch, buildCommit, buildTime)
      ).asRight[Unit]
        .pure[I]
    }

  def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(versionEndpoint)

}

object BuildInfoController {

  final class Maker[I[_] : ContextShift : Concurrent : Timer]
    extends Lifecycle.Of(Lifecycle.pure(BuildInfoController[I]()))
}
