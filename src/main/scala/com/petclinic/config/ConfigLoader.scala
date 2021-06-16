package com.petclinic.config

import cats.effect.{ContextShift, Sync}
import pureconfig.{ConfigReader, ConfigSource}
import pureconfig.error.ConfigReaderException
import tofu.BlockExec
import tofu.syntax.feither._
import tofu.syntax.scoped._

trait ConfigLoader[I[_]] {
  def loadConfig(namespace: String): I[AppConfig]
}

object ConfigLoader {

  final class Impl[I[_] : Sync : ContextShift : BlockExec] extends ConfigLoader[I] {
    def loadConfig(namespace: String): I[AppConfig] =
      withBlocker(blocker =>
        blocker
          .delay(ConfigSource.default.at(namespace).cursor())
          .flatMapIn(ConfigReader[AppConfig].from)
          .leftMapIn(ConfigReaderException[AppConfig](_))
          .reRaise
      )
  }

}
