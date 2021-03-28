package com.petclinic.logger

import cats.effect.Sync
import com.petclinic.logger.Logger.{Log, LogCtx}
import distage.Lifecycle
import izumi.logstage.api.logger.AbstractLogger
import logstage.strict.LogstageCatsStrict.{withDynamicContextStrict => withDynamicContext}
import tofu.WithContext
import tofu.syntax.context._

object CtxLog {

  final class Maker[F[_] : Sync](logger: AbstractLogger)(implicit WC: WithContext[F, LogCtx])
    extends Lifecycle.Of(Lifecycle.pure(make[F](logger)))

  private def make[F[_] : Sync](logger: AbstractLogger)(implicit WC: WithContext[F, LogCtx]): Log[F] =
    withDynamicContext(logger)(context[F])

}
