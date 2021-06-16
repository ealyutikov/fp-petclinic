import java.time.Instant
import Key.{buildNumber, projectVersionMajor, projectVersionMinor, swaggerUiWebJarVersion, _}
import com.typesafe.sbt.GitPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo.{BuildInfoPlugin, _}

object BuildInfo {

  final implicit class BuildInfoOps(val project: Project) extends AnyVal {
    def withBuildInfo: Project =
      project
        .enablePlugins(BuildInfoPlugin)
        .settings(
          buildCommit := git.gitHeadCommit.value.getOrElse("unknown"),
          buildBranch := sys.props.getOrElse("BUILD_BRANCH", git.gitCurrentBranch.value),
          buildTime := Instant.now,
          buildNumber := sys.props.getOrElse("BUILD_NUMBER", "0").toInt,
          swaggerUiWebJarVersion := Dependencies.Versions.swagger
        )
        .settings(
          buildInfoKeys := {
            Seq[BuildInfoKey](
              name,
              version,
              scalaVersion,
              sbtVersion,
              buildCommit,
              buildBranch,
              buildTime,
              buildNumber,
              projectVersionMinor,
              projectVersionMajor,
              swaggerUiWebJarVersion
            )
          },
          buildInfoPackage := "com.petclinic",
          buildInfoOptions += BuildInfoOption.ToMap,
          buildInfoOptions += BuildInfoOption.ToJson
        )
  }

}
