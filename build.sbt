import BuildInfo.BuildInfoOps
import DockerSettings.DockerSettingsOps
import Key.{projectVersionMajor, projectVersionMinor}

lazy val app = (project in file("."))
  .settings(
    name := "fp-petclinic",
    projectVersionMajor := 0,
    projectVersionMinor := 1,
    version := s"${projectVersionMajor.value}.${projectVersionMinor.value}",
    scalaVersion := "2.13.6",
    libraryDependencies ++= Seq(Dependencies.Dependencies, CompilerPlugins.Plugins).flatten,
    fork := true,
    Global / cancelable := true,
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    scalafmtDetailedError := true
  )
  .withDocker
  .withBuildInfo

addCommandAlias("prepare", "fmt; compile; test:compile; fmtCheck")
addCommandAlias("check", "fixCheck; fmtCheck")
addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fmtCheck", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")
