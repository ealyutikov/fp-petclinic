package com.petclinic.cotroller

import cats.effect.{Blocker, ContextShift, Sync}
import cats.syntax.reducible._
import com.petclinic.config.AppConfig.Swagger
import com.petclinic.util.aliases.Nel
import io.circe.Printer
import io.circe.syntax._
import izumi.distage.model.definition.Lifecycle
import org.http4s.{HttpRoutes, StaticFile, Uri}
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Location
import sttp.tapir.openapi.{Info, Server}
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.Endpoint
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe._
import tofu.BlockExec
import tofu.syntax.scoped.withBlocker
import com.petclinic.BuildInfo
import com.petclinic.BuildInfo.swaggerUiWebJarVersion

final case class SwaggerController[I[_] : Sync : ContextShift](
  endpoints: List[Endpoint[_, _, _, _]],
  servers: List[Server],
  blocker: Blocker
) {
  private val dsl = Http4sDsl[I]
  import dsl._

  private val jsonPrinter: Printer = Printer.spaces2.copy(dropNullValues = true)

  private lazy val openApi = OpenAPIDocsInterpreter()
    .toOpenAPI(endpoints, Info(BuildInfo.name, BuildInfo.version))
    .copy(servers = servers)

  private val resourceMapper = Map(
    "swagger-ui"                      -> "index.html",
    "swagger-ui.css"                  -> "swagger-ui.css",
    "swagger-ui-bundle.js"            -> "swagger-ui-bundle.js",
    "swagger-ui-standalone-preset.js" -> "swagger-ui-standalone-preset.js"
  )

  private def swaggerUIRedirect: HttpRoutes[I] = HttpRoutes.of[I] { case GET -> Root / "swagger" =>
    val redirectUri = Uri.unsafeFromString(s"/swagger-ui?url=/swagger.yml#/")
    PermanentRedirect(Location(redirectUri))
  }

  private def swaggerUI: HttpRoutes[I] = HttpRoutes.of[I] {
    case request @ GET -> Root / resourceKey if resourceMapper.keys.exists(_ == resourceKey) =>
      StaticFile
        .fromResource(
          name = s"/META-INF/resources/webjars/swagger-ui/$swaggerUiWebJarVersion/${resourceMapper(resourceKey)}",
          blocker = blocker,
          req = Some(request)
        )
        .getOrElseF(Sync[I].raiseError(new RuntimeException))
  }

  private def swaggerYml: HttpRoutes[I] =
    HttpRoutes.of[I] {
      case GET -> Root / "docs" / "swagger.yml"  => Ok(openApi.toYaml)
      case GET -> Root / "docs" / "swagger.json" => Ok(openApi.asJson.printWith(jsonPrinter))
    }

  def routes: HttpRoutes[I] = Nel.of(swaggerYml, swaggerUI, swaggerUIRedirect).reduceK

}

object SwaggerController {
  final class Maker[I[_] : Sync : ContextShift](endpoints: List[Endpoint[_, _, _, _]], config: Swagger)(implicit
    B: BlockExec[I]
  ) extends Lifecycle.LiftF(
      withBlocker(blocker => Sync[I].pure(new SwaggerController[I](endpoints, config.servers, blocker)))
    )
}
