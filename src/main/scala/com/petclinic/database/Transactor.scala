package com.petclinic.database

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.petclinic.config.AppConfig.DbConfig
import com.petclinic.util.aliases.Trans
import distage.Lifecycle
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts.{cachedThreadPool, fixedThreadPool}

object Transactor {

  final class Maker[F[_] : Async : ContextShift](config: DbConfig) extends Lifecycle.Of(Lifecycle.fromCats(make[F](config)))

  private def make[F[_] : Async : ContextShift](db: DbConfig): Resource[F, Trans[F]] =
    for {
      ce <- fixedThreadPool[F](db.poolSize)
      te <- cachedThreadPool[F]
      blocker = Blocker.liftExecutionContext(te)
      xa <- HikariTransactor.newHikariTransactor(db.driverClassName, db.url, db.user, db.pass, ce, blocker)
    } yield xa.trans

}
