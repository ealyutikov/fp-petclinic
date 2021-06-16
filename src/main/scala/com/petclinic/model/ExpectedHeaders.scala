package com.petclinic.model

import com.petclinic.context.{RequestId, SessionId}
import sttp.tapir._

final case class ExpectedHeaders(requestId: RequestId, sessionId: Option[SessionId])

object ExpectedHeaders {
  val endpointInput: EndpointInput[ExpectedHeaders] =
    header[RequestId](RequestId.httpHeader)
      .and(header[Option[SessionId]](SessionId.httpHeader))
      .mapTo[ExpectedHeaders]
}
