import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.Keys.{dockerBaseImage, dockerUpdateLatest}
import sbt.Project

object DockerSettings {

  final implicit class DockerSettingsOps(private val project: Project) extends AnyVal {
    def withDocker: Project =
      project
        .enablePlugins(JavaAppPackaging, DockerPlugin)
        .settings(
          dockerUpdateLatest := true,
          dockerBaseImage := "adoptopenjdk/openjdk11:alpine-jre"
        )
  }

}
