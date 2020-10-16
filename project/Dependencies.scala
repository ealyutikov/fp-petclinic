import sbt._

object Dependencies {

  object Versions {
    val tapir = "0.17.0-M2"
    val cats = "2.2.0"
    val doobie = "0.9.2"
    val flyway = "7.0.3"
    val http4s = "1.0.0-M4"
    val pureconfig = "0.14.0"
    val circe = "0.12.3"
    val tofu = "0.8.0"
    val distage = "0.10.19"
    val refined = "0.9.17"
    val newtype = "0.4.4"
    val catsMtl = "1.0.0"
    val mouse = "0.25"
  }

  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-refined"
  ).map(_ % Versions.circe)

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-refined",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"
  ).map(_ % Versions.tapir)

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-postgres-circe",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-refined"
  ).map(_ % Versions.doobie)

  val cats = Seq("org.typelevel" %% "cats-core" % Versions.cats, "org.typelevel" %% "cats-effect" % Versions.cats)

  val http4s = Seq("org.http4s" %% "http4s-circe" % Versions.http4s, "org.http4s" %% "http4s-dsl" % Versions.http4s)

  val flyway = Seq("org.flywaydb" % "flyway-core" % Versions.flyway)

  val pureConfig = Seq(
    "com.github.pureconfig" %% "pureconfig",
    "com.github.pureconfig" %% "pureconfig-cats-effect"
  ).map(_ % Versions.pureconfig)

  val distage = Seq(
    "io.7mind.izumi" %% "distage-core" % Versions.distage,
    "io.7mind.izumi" %% "distage-testkit-scalatest" % Versions.distage % Test,
    "io.7mind.izumi" %% "distage-framework-docker" % Versions.distage % Test,
    "io.7mind.izumi" %% "distage-extension-config" % Versions.distage % Test
  )

  val tofu = Seq("ru.tinkoff" %% "tofu-core" % Versions.tofu)

  val refined = Seq("eu.timepit" %% "refined-cats" % Versions.refined)

  val newtype = Seq("io.estatico" %% "newtype" % Versions.newtype)

  val mouse = Seq("org.typelevel" %% "mouse" % Versions.mouse)

  val catsMtl = Seq("org.typelevel" %% "cats-mtl" % Versions.catsMtl)

}
