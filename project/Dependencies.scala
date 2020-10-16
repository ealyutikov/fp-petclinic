import sbt._

object Dependencies {

  object Versions {
    val tapir = "0.17.18"
    val cats = "2.4.2"
    val catsEffect = "2.3.3"
    val tagless = "0.12"
    val doobie = "0.9.2"
    val flyway = "7.5.1"
    val pureconfig = "0.14.0"
    val circe = "0.12.3"
    val tofu = "0.10.0"
    val izumi = "1.0.3"
    val refined = "0.9.17"
    val newtype = "0.4.4"
    val catsMtl = "1.0.0"
    val mouse = "0.25"
    val derevo = "0.11.6"
  }

  val derevo = Seq(
    "org.manatki" %% "derevo-cats",
    "org.manatki" %% "derevo-cats-tagless",
    "org.manatki" %% "derevo-circe-magnolia"
  ).map(_ % Versions.derevo)

  val tagless = Seq(
    "org.typelevel" %% "cats-tagless-core"   % Versions.tagless,
    "org.typelevel" %% "cats-tagless-macros" % Versions.tagless
  )

  val circe = Seq("io.circe" %% "circe-refined" % Versions.circe)

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

  val cats = Seq("org.typelevel" %% "cats-core" % Versions.cats, "org.typelevel" %% "cats-effect" % Versions.catsEffect)

  val flyway = Seq("org.flywaydb" % "flyway-core" % Versions.flyway)

  val pureconfig = Seq("com.github.pureconfig" %% "pureconfig", "com.github.pureconfig" %% "pureconfig-cats-effect")
    .map(_ % Versions.pureconfig)

  val izumi = Seq(
    "io.7mind.izumi" %% "distage-core",
    "io.7mind.izumi" %% "logstage-core",
    "io.7mind.izumi" %% "logstage-rendering-circe", // Json output
    "io.7mind.izumi" %% "logstage-adapter-slf4j", // Router from Slf4j to LogStage
    "io.7mind.izumi" %% "distage-extension-logstage", // LogStage integration with DIStage
    "io.7mind.izumi" %% "logstage-sink-slf4j" // Router from LogStage to Slf4J
  ).map(_ % Versions.izumi)

  val izumiTest = Seq(
    "io.7mind.izumi" %% "distage-testkit-scalatest",
    "io.7mind.izumi" %% "distage-framework-docker",
    "io.7mind.izumi" %% "distage-extension-config"
  ).map(_ % Versions.izumi % Test)

  val tofu = Seq("ru.tinkoff" %% "tofu-core" % Versions.tofu)

  val refined = Seq("eu.timepit" %% "refined-cats" % Versions.refined)

  val newtype = Seq("io.estatico" %% "newtype" % Versions.newtype)

  val mouse = Seq("org.typelevel" %% "mouse" % Versions.mouse)

  val catsMtl = Seq("org.typelevel" %% "cats-mtl" % Versions.catsMtl)

}
