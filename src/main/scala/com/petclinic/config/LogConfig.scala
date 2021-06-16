package com.petclinic.config

import com.petclinic.config.LogConfig.Levels
import derevo.derive
import derevo.pureconfig.config
import izumi.logstage.api.rendering.RenderingOptions
import pureconfig.generic.semiauto.deriveReader
import pureconfig.ConfigReader

@derive(config)
final case class LogConfig(levels: Levels, options: Option[RenderingOptions], json: Boolean)

object LogConfig {
  type Paths = Option[List[String]]

  implicit val renderingOptionsReader: ConfigReader[RenderingOptions] = deriveReader

  @derive(config)
  final case class Levels(trace: Paths, debug: Paths, info: Paths, warn: Paths, error: Paths, crit: Paths)
}
