import java.time.Instant

import sbt.{SettingKey, taskKey}

object Key {
  val buildBranch = SettingKey[String]("buildBranch", "Git branch")
  val buildCommit = SettingKey[String]("buildCommit", "Git commit")
  val buildDate = SettingKey[String]("buildNumber", "Project current build date")
  val buildTime = taskKey[Instant]("Time of this build")
}
