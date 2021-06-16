import sbt.{taskKey, SettingKey}

import java.time.Instant

object Key {
  val buildBranch = SettingKey[String]("buildBranch", "Git branch.")
  val buildCommit = SettingKey[String]("buildCommit", "Git commit.")
  val buildNumber = SettingKey[Int]("buildNumber", "Project current build version")
  val projectVersionMajor = SettingKey[Int]("projectVersionMajor", "project version major part")
  val projectVersionMinor = SettingKey[Int]("projectVersionMinor", "project version minor part")
  val buildTime = taskKey[Instant]("Time of this build")
  val swaggerUiWebJarVersion = SettingKey[String]("swaggerUiWebJarVersion", "SwaggerUI version")
}
