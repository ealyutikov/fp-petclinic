package com.petclinic.logger

import cats.effect.Sync
import distage.Lifecycle
import izumi.logstage.api.logger.AbstractLogger
import izumi.logstage.api.Log.CustomContext
import logstage.strict.LogIOStrict
import logstage.strict.LogstageCatsStrict.{withDynamicContextStrict => withDynamicContext}
import tofu.WithContext
import tofu.syntax.context._

object CtxLog {

  final class Maker[F[_] : Sync](logger: AbstractLogger)(implicit WC: WithContext[F, CustomContext])
    extends Lifecycle.Of(Lifecycle.pure(make[F](logger)))

  private def make[F[_] : Sync](logger: AbstractLogger)(implicit WC: WithContext[F, CustomContext]): LogIOStrict[F] =
    withDynamicContext(logger)(context[F])

}
