package com.petclinic

import cats.data.ReaderT
import cats.effect.{CancelToken, Concurrent, ConcurrentEffect, ExitCase, Fiber, IO, SyncIO}
import tofu.{WithContext, WithLocal}

package object ctx {

  val EmptyCtx = Map.empty[String, String]

  type Ctx = Map[String, String]

  type AskCtx[F[_]] = F WithContext Ctx

  type LocalCtx[F[_]] = F WithLocal Ctx

  type Env[F[_], A] = ReaderT[F, Ctx, A]
  val Env: ReaderT.type = ReaderT

  implicit def deriveConcurrentEffectForEnv[F[_]](implicit F: ConcurrentEffect[F]): ConcurrentEffect[Env[F, *]] =
    new ConcurrentEffect[Env[F, *]] {

      private val concurrent: Concurrent[Env[F, *]] = Concurrent.catsKleisliConcurrent[F, Ctx](F)

      override def runCancelable[A](
        fa: Env[F, A]
      )(cb: Either[Throwable, A] => IO[Unit]): SyncIO[CancelToken[Env[F, *]]] =
        fa.mapF(F.runCancelable(_)(cb).map(Env.liftF(_): CancelToken[Env[F, *]])).run(EmptyCtx)

      override def runAsync[A](fa: Env[F, A])(cb: Either[Throwable, A] => IO[Unit]): SyncIO[Unit] =
        fa.mapF(F.runAsync(_)(cb)).run(EmptyCtx)

      override def start[A](fa: Env[F, A]): Env[F, Fiber[Env[F, *], A]] =
        concurrent.start(fa)

      override def racePair[A, B](
        fa: Env[F, A],
        fb: Env[F, B]
      ): Env[F, Either[(A, Fiber[Env[F, *], B]), (Fiber[Env[F, *], A], B)]] =
        concurrent.racePair(fa, fb)

      override def async[A](k: (Either[Throwable, A] => Unit) => Unit): Env[F, A] =
        concurrent.async(k)

      override def asyncF[A](k: (Either[Throwable, A] => Unit) => Env[F, Unit]): Env[F, A] =
        concurrent.asyncF(k)

      override def suspend[A](thunk: => Env[F, A]): Env[F, A] =
        concurrent.suspend(thunk)

      override def bracketCase[A, B](
        acquire: Env[F, A]
      )(use: A => Env[F, B])(release: (A, ExitCase[Throwable]) => Env[F, Unit]): Env[F, B] =
        concurrent.bracketCase(acquire)(use)(release)

      override def raiseError[A](e: Throwable): Env[F, A] =
        concurrent.raiseError(e)

      override def handleErrorWith[A](fa: Env[F, A])(f: Throwable => Env[F, A]): Env[F, A] =
        concurrent.handleErrorWith(fa)(f)

      override def pure[A](x: A): Env[F, A] =
        concurrent.pure(x)

      override def flatMap[A, B](fa: Env[F, A])(f: A => Env[F, B]): Env[F, B] =
        concurrent.flatMap(fa)(f)

      override def tailRecM[A, B](a: A)(f: A => Env[F, Either[A, B]]): Env[F, B] =
        concurrent.tailRecM(a)(f)
    }

}
