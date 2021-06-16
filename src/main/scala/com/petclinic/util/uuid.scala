package com.petclinic.util

import cats.instances.uuid._
import cats.syntax.show._
import izumi.logstage.api.rendering.LogstageCodec

import java.util.UUID

object uuid {
  implicit val uuidLogstageCodec: LogstageCodec[UUID] = LogstageCodec[String].contramap(_.show)
}
