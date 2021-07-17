package com.petclinic.database

import cats.effect.{Async, ContextShift, Resource, Sync}
import cats.implicits.catsSyntaxOptionId
import com.petclinic.config.DbConfig
import com.petclinic.util.ec
import com.petclinic.util.resource._
import doobie.Transactor
import doobie.hikari.HikariTransactor
import izumi.distage.model.definition.Lifecycle

object DBTransactor {
  private val defaultConnectECThreadCount = 10
  private val maxConnectECThreadCount = 32

  final class Maker[I[_] : Async : ContextShift](dbConfig: DbConfig) extends Lifecycle.OfCats(makeResource[I](dbConfig))

  def makeResource[I[_] : Async : ContextShift](dbConfig: DbConfig): Resource[I, Transactor[I]] = {
    import dbConfig._
    import dbConfig.connection._
    val ceSize = hikari.flatMap(_.maximumPoolSize).getOrElse(defaultConnectECThreadCount) min maxConnectECThreadCount
    for {
      ce <- ec.fixedThreadPool[I](ceSize, ec.namedThreadFactory("hikari-get-connection").some)
      te <- ec.blocker[I]("hikari-operation")
      xa <- HikariTransactor.newHikariTransactor(driverClassName, url, user, pass, ce, te)
      _ <- xa.configure { ds =>
        Sync[I].delay {
          hikari.foreach { h =>
            h.maximumPoolSize.foreach(ds.setMaximumPoolSize)
            h.maxLifetime.foreach(mlt => ds.setMaxLifetime(mlt.toMillis))
            h.connectionTimeout.foreach(ct => ds.setConnectionTimeout(ct.toMillis))
            h.connectionInitSql.foreach(ini => ds.setConnectionInitSql(ini))
          }
        }
      }.toResource
    } yield xa
  }

}
