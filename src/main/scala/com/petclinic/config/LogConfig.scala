package com.petclinic.config

import cats.syntax.option._
import izumi.logstage.api.rendering.RenderingOptions

final case class LogConfig(
  levels: LogConfig.Levels,
  options: Option[RenderingOptions] = none[RenderingOptions],
  json: Boolean = false
)

object LogConfig {

  type Paths = Option[List[String]]

  final case class Levels(
    trace: Paths = none[List[String]],
    debug: Paths = none[List[String]],
    info: Paths = none[List[String]],
    warn: Paths = none[List[String]],
    error: Paths = none[List[String]],
    crit: Paths = none[List[String]]
  )
}
