package com.petclinic.context

import izumi.logstage.api.rendering.LogstageCodec
import sttp.tapir.Codec

final case class SessionId(value: String) extends AnyVal

object SessionId {
  val httpHeader = "X-Session-ID"
  val logField: String = "sessionId"
  implicit val sessionIdLogstageCodec: LogstageCodec[SessionId] = LogstageCodec[String].contramap(_.value)
  implicit val sessionIdTapirCodec: Codec.PlainCodec[SessionId] = Codec.string.map(SessionId(_))(_.value)
}
