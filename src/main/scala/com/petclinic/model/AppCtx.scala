package com.petclinic.model

import com.petclinic.logging.Logger.LogCtx
import com.petclinic.model.context.{RequestId, SessionId}
import izumi.logstage.api.rendering.StrictEncoded
import tofu.optics.Extract

final case class AppCtx(requestId: RequestId, sessionId: Option[SessionId]) {
  lazy val asLogCtx: LogCtx = {
    val builder = Map.newBuilder[String, StrictEncoded]
    builder += RequestId.logField -> requestId
    builder ++= sessionId.map(sid => SessionId.logField -> sid)
    LogCtx.fromMap(builder.result())
  }
}

object AppCtx {
  implicit def extractLogCtx: AppCtx Extract LogCtx = _.asLogCtx
}
