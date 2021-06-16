package com.petclinic.config

import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint

trait WithHints {
  implicit def hint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))
}
