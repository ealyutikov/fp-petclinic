package com.petclinic.metrics

import cats.effect.Sync
import distage.{ModuleDef, TagK}
import io.chrisdavenport.epimetheus.{Collector, CollectorRegistry}
import tofu.syntax.monadic._

final class MetricsModule[I[_] : TagK] extends ModuleDef {
  make[CollectorRegistry[I]].fromEffect[I, CollectorRegistry[I]] { implicit F: Sync[I] =>
    val cr = CollectorRegistry.defaultRegistry[I]
    Collector.Defaults.registerDefaults(cr).as(cr)
  }
}
