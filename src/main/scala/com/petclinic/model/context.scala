package com.petclinic.model

import io.estatico.newtype.macros.newtype
import izumi.logstage.api.rendering.LogstageCodec
import java.util.UUID

object context {

  @newtype
  final case class RequestId(value: UUID)
  object RequestId {
    val httpHeader = "X-Request-ID"
    val logField: String = "requestId"
    implicit val requestIdLogstageCodec: LogstageCodec[RequestId] = LogstageCodec[String].contramap(_.value.toString)
  }

  @newtype
  final case class SessionId(value: UUID)
  object SessionId {
    val httpHeader = "X-Session-ID"
    val logField: String = "sessionId"
    implicit val sessionIdLogstageCodec: LogstageCodec[SessionId] = LogstageCodec[String].contramap(_.value.toString)
  }

}
