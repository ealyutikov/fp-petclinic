package com.petclinic.database

import cats.effect.{Async, ContextShift, Resource}
import com.petclinic.config.AppConfig.DbConfig
import com.petclinic.util.aliases.Trans
import com.petclinic.util.ExecutionContexts._
import distage.Lifecycle
import doobie.hikari.HikariTransactor

object Transactor {

  final class Maker[F[_] : Async : ContextShift](config: DbConfig)
    extends Lifecycle.Of(Lifecycle.fromCats(make[F](config)))

  private def make[F[_] : Async : ContextShift](db: DbConfig): Resource[F, Trans[F]] =
    for {
      ce      <- fixedThreadPool(db.poolSize, "hikari-get-connection")
      blocker <- blocker("hikari-blocker")
      xa      <- HikariTransactor.newHikariTransactor(db.driverClassName, db.url, db.user, db.pass, ce, blocker)
    } yield xa.trans

}
