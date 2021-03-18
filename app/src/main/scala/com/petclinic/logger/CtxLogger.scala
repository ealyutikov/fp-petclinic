package com.petclinic.logger

//import cats.Monad
//import cats.effect.Sync
//import com.petclinic.ctx.{AskCtx, Ctx}
//import izumi.distage.model.definition.Lifecycle
//import izumi.fundamentals.platform.language.CodePositionMaterializer
//import izumi.logstage.api.Log._
//import izumi.logstage.api.logger.AbstractLogger
//import izumi.logstage.api.rendering.AnyEncoded
//import logstage.{Level, LogIO}
//import tofu.syntax.context.askF

object CtxLogger {

//  final class Maker[F[_] : Sync : AskCtx](logger: AbstractLogger) extends Lifecycle.Of(Lifecycle.pure(make(logger)))
//
//  private def make[F[_] : Sync : AskCtx](logger: AbstractLogger): LogIO[F] = new Impl(LogIO.fromLogger(logger))
//
//  private final class Impl[F[_] : Monad : AskCtx](logger: LogIO[F]) extends LogIO[F] {
//
//    implicit def LogCtx2CustomContext(logCtx: Ctx): CustomContext =
//      CustomContext.fromMap(logCtx.map(AnyEncoded.toPair))
//
//    def log(entry: Entry): F[Unit] = askF(logger.withCustomContext(_: Ctx).log(entry))
//
//    def log(logLevel: Level)(messageThunk: => Message)(implicit pos: CodePositionMaterializer): F[Unit] =
//      askF(logger.withCustomContext(_: Ctx).log(logLevel)(messageThunk))
//
//    def withCustomContext(context: CustomContext): LogIO[F] = logger.withCustomContext(context)
//
//    def unsafeLog(entry: Entry): F[Unit] =
//      askF(logger.withCustomContext(_: Ctx).unsafeLog(entry))
//
//    def acceptable(loggerId: LoggerId, logLevel: Level): F[Boolean] =
//      askF(logger.withCustomContext(_: Ctx).acceptable(loggerId, logLevel))
//
//    def acceptable(logLevel: Level)(implicit pos: CodePositionMaterializer): F[Boolean] =
//      askF(logger.withCustomContext(_: Ctx).acceptable(logLevel))
//
//    def createEntry(logLevel: Level, message: Message)(implicit pos: CodePositionMaterializer): F[Entry] =
//      askF(logger.withCustomContext(_: Ctx).createEntry(logLevel, message))
//
//    def createContext(logLevel: Level, customContext: CustomContext)(implicit
//      pos: CodePositionMaterializer
//    ): F[Context] =
//      askF(logger.withCustomContext(_: Ctx).createContext(logLevel, customContext))
//
//  }

}
