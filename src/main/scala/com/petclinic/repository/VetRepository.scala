package com.petclinic.repository

import cats.Apply
import com.petclinic.logger.Logger.{log, Log}
import com.petclinic.model.Vet
import com.petclinic.util.aliases.Trans
import derevo.derive
import distage.Lifecycle
import doobie.{ConnectionIO, LogHandler}
import doobie.implicits._
import tofu.higherKind.derived.representableK
import tofu.higherKind.Mid
import tofu.syntax.monadic._

import doobie.refined.implicits._
import cats.tagless.syntax.functorK._

@derive(representableK)
trait VetRepository[DB[_]] {
  def findAll(): DB[List[Vet]]
}

object VetRepository {

  final class Maker[F[_] : Apply : Log](xa: Trans[F], lh: LogHandler)
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[F]
      val impl = new DoobieImpl()(lh).mapK(xa)
      mid attach impl
    })

  private final class DoobieImpl(implicit lh: LogHandler) extends VetRepository[ConnectionIO] {
    override def findAll(): ConnectionIO[List[Vet]] =
      sql"""SELECT * FROM vets;"""
        .stripMargin
        .query[Vet]
        .to[List]
  }

  final class LoggingMid[F[_] : Apply : Log] extends VetRepository[Mid[F, *]] {
    override def findAll(): Mid[F, List[Vet]] =
      log.info(s"Started db processing") *> _ <* log.info(s"Finished db processing")
  }

}
