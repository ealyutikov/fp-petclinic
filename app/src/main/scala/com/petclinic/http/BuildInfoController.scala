package com.petclinic.http

import cats.effect.{Concurrent, ContextShift, Timer}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import com.petclinic.BuildInfo._
import com.petclinic.http.BuildInfoController.{AppVersion, BuildInfoResponse, BuildVersion}
import distage.Lifecycle
import io.circe.generic.JsonCodec
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

final case class BuildInfoController[F[_] : ContextShift : Concurrent : Timer]() extends Controller[F] {

  private val versionEndpoint =
    endpoint
      .get
      .in("version")
      .out(jsonBody[BuildInfoResponse])

  override def routes: HttpRoutes[F] =
    Http4sServerInterpreter.toRoutes(versionEndpoint) { _ =>
      BuildInfoResponse(
        name,
        AppVersion(version),
        BuildVersion(buildNumber, buildBranch, buildCommit, buildTime)
      ).asRight[Unit]
        .pure[F]
    }

}

object BuildInfoController {

  final class Maker[F[_] : ContextShift : Concurrent : Timer]
    extends Lifecycle.Of(Lifecycle.pure(BuildInfoController[F]()))

  @JsonCodec
  final case class AppVersion(value: String)

  @JsonCodec
  final case class BuildVersion(number: String, branch: String, commit: String, time: String)

  @JsonCodec
  final case class BuildInfoResponse(name: String, version: AppVersion, build: BuildVersion)

}
