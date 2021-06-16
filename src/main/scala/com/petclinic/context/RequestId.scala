package com.petclinic.context

import izumi.logstage.api.rendering.LogstageCodec
import sttp.tapir.Codec

final case class RequestId(value: String) extends AnyVal

object RequestId {
  val httpHeader = "X-Request-ID"
  val logField: String = "requestId"
  implicit val requestIdLogstageCodec: LogstageCodec[RequestId] = LogstageCodec[String].contramap(_.value)
  implicit val requestIdTapirCodec: Codec.PlainCodec[RequestId] = Codec.string.map(RequestId(_))(_.value)
}
