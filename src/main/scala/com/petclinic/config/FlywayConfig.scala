package com.petclinic.config

import derevo.derive
import derevo.pureconfig.config

@derive(config)
final case class FlywayConfig(locations: Vector[String], enableMigrations: Boolean = false)

object FlywayConfig extends WithHints
