import sbt._

object Dependencies {

  object Versions {
    val tapir = "0.17.19"
    val cats = "2.4.2"
    val catsEffect = "2.3.3"
    val tagless = "0.12"
    val doobie = "0.12.1"
    val flyway = "7.5.1"
    val pureconfig = "0.14.0"
    val circe = "0.13.0"
    val tofu = "0.10.0"
    val izumi = "1.0.3"
    val refined = "0.9.21"
    val newtype = "0.4.4"
    val catsMtl = "1.0.0"
    val mouse = "1.0.0"
    val derevo = "0.12.2"
    val zio = "1.0.5"
    val zioCatz = "2.3.1.0"
  }

  val derevo = Seq("tf.tofu" %% "derevo-cats", "tf.tofu" %% "derevo-cats-tagless", "tf.tofu" %% "derevo-circe-magnolia")
    .map(_ % Versions.derevo)

  val tagless = Seq(
    "org.typelevel" %% "cats-tagless-core"   % Versions.tagless,
    "org.typelevel" %% "cats-tagless-macros" % Versions.tagless
  )

  val circe = Seq("io.circe" %% "circe-refined" % Versions.circe)

  val tapir = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-core",
    "com.softwaremill.sttp.tapir" %% "tapir-cats",
    "com.softwaremill.sttp.tapir" %% "tapir-refined",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-enumeratum",
    "com.softwaremill.sttp.tapir" %% "tapir-newtype",
    "com.softwaremill.sttp.tapir" %% "tapir-derevo",
    // "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs",
    //"com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml",
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

  val tofu = Seq(
    "tf.tofu" %% "tofu-core",
    "tf.tofu" %% "tofu-zio-core",
    "tf.tofu" %% "tofu-derivation",
    "tf.tofu" %% "tofu-doobie"
  ).map(_ % Versions.tofu)

  val refined = Seq("eu.timepit" %% "refined-cats" % Versions.refined)

  val newtype = Seq("io.estatico" %% "newtype" % Versions.newtype)

  val mouse = Seq("org.typelevel" %% "mouse" % Versions.mouse)

  val catsMtl = Seq("org.typelevel" %% "cats-mtl" % Versions.catsMtl)

  val zio = Seq("dev.zio" %% "zio" % Versions.zio, "dev.zio" %% "zio-interop-cats" % Versions.zioCatz)

}
