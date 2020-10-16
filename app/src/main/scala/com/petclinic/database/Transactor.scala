package com.petclinic.database

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.petclinic.config.AppConfig.DbConfig
import com.petclinic.util.aliases.Trans
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts.{cachedThreadPool, fixedThreadPool}

object Transactor {

  def apply[F[_] : Async : ContextShift](db: DbConfig): Resource[F, Trans[F]] =
    for {
      ce <- fixedThreadPool[F](db.poolSize)
      te <- cachedThreadPool[F]
      blocker = Blocker.liftExecutionContext(te)
      xa <- HikariTransactor.newHikariTransactor(db.driverClassName, db.url, db.user, db.pass, ce, blocker)
    } yield xa.trans

}
