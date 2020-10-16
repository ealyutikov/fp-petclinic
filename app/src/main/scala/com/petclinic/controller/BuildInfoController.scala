package com.petclinic.controller

import cats.data.NonEmptyList
import cats.effect.{ContextShift, Sync}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import com.petclinic.controller.Controller._
import org.http4s.HttpRoutes
import com.petclinic.BuildInfo._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._
import sttp.tapir.{Endpoint, _}

final case class BuildInfoController[F[_] : Sync : ContextShift]() extends Controller[F] {

  private val versionEndpoint =
    sttp.tapir.endpoint
      .get
      .description("App version")
      .in("version")
      .out(jsonBody[BuildInfoResponse])

  override def routes: HttpRoutes[F] =
    versionEndpoint.toRoutes(
      _ =>
        BuildInfoResponse(
          name,
          AppVersion(version),
          //BuildVersion(buildNumber, buildBranch, buildCommit, buildTime)
          BuildVersion(1, "null", "null", "null")
        ).asRight[Unit]
          .pure[F]
    )

  override def endpoints: NonEmptyList[Endpoint[_, _, _, _]] = NonEmptyList.of(versionEndpoint)

}
