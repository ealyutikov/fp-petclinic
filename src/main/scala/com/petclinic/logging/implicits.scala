package com.petclinic.logging

import cats.tagless.FunctorK
import cats.~>
import com.petclinic.logging.Logger.{Log, LogCtx}
import izumi.fundamentals.platform.language.CodePositionMaterializer
import izumi.logstage.api.Log._
import logstage.Level

object implicits {
  implicit def logFunctorK: FunctorK[Log] =
    new FunctorK[Log] {
      def mapK[F[_], G[_]](af: Log[F])(fk: F ~> G): Log[G] = logMapK(af)(fk)
    }

  private def logMapK[F[_], G[_]](af: Log[F])(fk: F ~> G): Log[G] =
    new Log[G] {
      def log(entry: Entry): G[Unit] = fk(af.log(entry))
      def log(logLevel: Level)(messageThunk: => Message)(implicit pos: CodePositionMaterializer): G[Unit] =
        fk(af.log(logLevel)(messageThunk))
      def withCustomContext(context: LogCtx): Log[G] = logMapK(af.withCustomContext(context))(fk)
      def unsafeLog(entry: Entry): G[Unit] = fk(af.unsafeLog(entry))
      def acceptable(loggerId: LoggerId, logLevel: Level): G[Boolean] = fk(af.acceptable(loggerId, logLevel))
      def acceptable(logLevel: Level)(implicit pos: CodePositionMaterializer): G[Boolean] = fk(af.acceptable(logLevel))
      def createEntry(logLevel: Level, message: Message)(implicit pos: CodePositionMaterializer): G[Entry] =
        fk(af.createEntry(logLevel, message))
      def createContext(logLevel: Level, customContext: LogCtx)(implicit
        pos: CodePositionMaterializer
      ): G[Context] = fk(af.createContext(logLevel, customContext))
    }
}
