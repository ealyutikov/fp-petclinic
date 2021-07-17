package com.petclinic.database

import cats.Monad
import cats.effect.BracketThrow
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.petclinic.config.DbConfig
import com.petclinic.logging.Logger._
import distage.Lifecycle
import doobie.Transactor

trait DbWarmer[I[_]] {
  def warmUp: I[Unit]
}

object DbWarmer {

  final class Maker[I[_] : BracketThrow : Log](trans: Transactor[I], config: DbConfig)
    extends Lifecycle.Of(Lifecycle.pure(new Impl[I](trans, config)))

  final class Impl[I[_] : BracketThrow : Log](transactor: Transactor[I], config: DbConfig) extends DbWarmer[I] {
    def warmUp: I[Unit] = {
      import doobie.implicits._
      Monad[I].whenA(config.warmUp) {
        for {
          _ <- log.info("Starting warm up")
          xa = transactor.trans
          _ <- xa(sql"select 42".query[Int].unique)
          _ <- log.info("Warm up finished")
        } yield ()
      }
    }
  }

}
