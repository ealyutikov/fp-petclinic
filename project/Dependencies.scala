import sbt._

object Dependencies {

  object Versions {
    val tapir = "0.18.0"
    val doobie = "0.13.4"
    val flyway = "7.11.0"
    val pureconfig = "0.16.0"
    val tofu = "0.10.2"
    val izumi = "1.0.6"
    val refined = "0.9.26"
    val newtype = "0.4.4"
    val mouse = "1.0.0"
    val derevo = "0.12.5"
    val zioCats = "2.5.1.0"
    val sttp = "3.2.3"
    val http4s = "0.22.0-RC1"
    val swagger = "3.51.1"
    val circe = "0.13.0"
  }

  val refined = Seq(
    "eu.timepit" %% "refined-cats" % Versions.refined,
    "eu.timepit" %% "refined-pureconfig" % Versions.refined,
    "io.circe" %% "circe-refined" % Versions.circe,
    "com.softwaremill.sttp.tapir" %% "tapir-refined" % Versions.tapir,
    "org.tpolecat" %% "doobie-refined" % Versions.doobie
  )

  val derevo = Seq(
    "tf.tofu" %% "derevo-cats",
    "tf.tofu" %% "derevo-cats-tagless",
    "tf.tofu" %% "derevo-circe-magnolia",
    "tf.tofu" %% "derevo-pureconfig"
  ).map(_ % Versions.derevo)

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-enumeratum",
    "com.softwaremill.sttp.tapir" %% "tapir-newtype",
    "com.softwaremill.sttp.tapir" %% "tapir-derevo",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"
  ).map(_ % Versions.tapir)

  val http4s = Seq(
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl",
    "org.http4s" %% "http4s-blaze-server"
  ).map(_ % Versions.http4s)

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-hikari"
  ).map(_ % Versions.doobie)

  val flyway = Seq("org.flywaydb" % "flyway-core" % Versions.flyway)

  val pureconfig = Seq("com.github.pureconfig" %% "pureconfig" % Versions.pureconfig)

  val izumi = Seq(
    "io.7mind.izumi" %% "distage-core",
    "io.7mind.izumi" %% "logstage-core",
    "io.7mind.izumi" %% "logstage-rendering-circe",
    "io.7mind.izumi" %% "logstage-adapter-slf4j",
    "io.7mind.izumi" %% "distage-extension-logstage",
    "io.7mind.izumi" %% "logstage-sink-slf4j"
  ).map(_ % Versions.izumi)

  val izumiTest = Seq(
    "io.7mind.izumi" %% "distage-testkit-scalatest",
    "io.7mind.izumi" %% "distage-framework-docker",
    "io.7mind.izumi" %% "distage-extension-config"
  ).map(_ % Versions.izumi % Test)

  val tofu = Seq(
    "tf.tofu" %% "tofu-core",
    "tf.tofu" %% "tofu-zio-core",
    "tf.tofu" %% "tofu-derivation",
    "tf.tofu" %% "tofu-doobie",
    "tf.tofu" %% "tofu-optics-core",
    "tf.tofu" %% "tofu-optics-macro"
  ).map(_ % Versions.tofu)

  val newtype = Seq("io.estatico" %% "newtype" % Versions.newtype)

  val mouse = Seq("org.typelevel" %% "mouse" % Versions.mouse)

  val zio = Seq("dev.zio" %% "zio-interop-cats" % Versions.zioCats)

  val swagger = Seq("org.webjars" % "swagger-ui" % Versions.swagger)

  val sttp = Seq(
    "com.softwaremill.sttp.client3" %% "core",
    "com.softwaremill.sttp.client3" %% "circe",
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats"
  ).map(_ % Versions.sttp)

  val Dependencies: Seq[ModuleID] = flyway ++ swagger ++ derevo ++ tapir ++ mouse ++ izumiTest ++
    doobie ++ pureconfig ++ tofu ++ zio ++ izumi ++ tofu ++ refined ++ http4s

}
