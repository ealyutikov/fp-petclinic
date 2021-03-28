import BuildInfo.BuildInfoOps
import CompilerPlugins._
import Dependencies._

// format: off

val appVersion = "0.1"

fork in ThisBuild := true

onChangedBuildSource in Global := ReloadOnSourceChanges

lazy val app = (project in file("."))
  .settings(
    name := "fp-petclinic",
    libraryDependencies ++= {
      cats ++ flyway ++ circe ++ newtype ++ derevo ++
        tapir ++ mouse ++ catsMtl ++ izumiTest ++
        doobie ++ pureconfig ++ tofu ++ zio ++
        izumi ++ tofu ++ circe ++ refined ++ tagless ++
        compileDependencies
    },
    version := appVersion,
    scalaVersion := "2.13.5",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    dockerUpdateLatest := true,
    dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre"
  ).enablePlugins(JavaAppPackaging, JDKPackagerPlugin, DockerPlugin, BuildInfoPlugin)
  .withBuildInfo


addCommandAlias("prepare", "fix; fmt; compile; test:compile")
addCommandAlias("check", "fixCheck; fmtCheck")
addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
addCommandAlias("fix", "all compile:scalafix test:scalafix")
addCommandAlias("fixCheck", "; compile:scalafix --check ; test:scalafix --check")

scalafixDependencies in ThisBuild += "com.nequissimus" %% "sort-imports" % "0.5.4"
mainClass in (Compile,run) := Some("com.petclinic.Application")
// format: on
