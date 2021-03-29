import java.time.Instant

import Key._
import com.typesafe.sbt.GitPlugin.autoImport._
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoKeys._
import sbtbuildinfo.{BuildInfoPlugin, _}

object BuildInfo {

  implicit class BuildInfoOps(private val project: Project) extends AnyVal {

    def withBuildInfo: Project =
      project
        .enablePlugins(BuildInfoPlugin)
        .settings(
          buildCommit := git.gitHeadCommit.value.getOrElse("unknown"),
          buildBranch := git.gitCurrentBranch.value,
          buildDate := git.gitHeadCommitDate.value.getOrElse("unknown"),
          buildTime := Instant.now
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
              buildDate
            )
          },
          buildInfoPackage := "com.petclinic",
          buildInfoOptions += BuildInfoOption.ToMap,
          buildInfoOptions += BuildInfoOption.ToJson
        )

  }

}
