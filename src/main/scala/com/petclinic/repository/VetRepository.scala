package com.petclinic.repository

import cats.{Apply, Monad}
import com.petclinic.logging.Logger.{log, Log}
import com.petclinic.model.Vet
import derevo.derive
import distage.Lifecycle
import doobie.{ConnectionIO, LogHandler}
import doobie.implicits._
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.higherKind.derived.representableK
import tofu.higherKind.Mid
import tofu.syntax.monadic._
import doobie.refined.implicits._

@derive(representableK)
trait VetRepository[DB[_]] {
  def findAll(): DB[List[Vet]]
}

object VetRepository {

  final class Maker[DB[_] : Monad : LiftConnectionIO : Log](implicit elh: EmbeddableLogHandler[DB])
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[DB]
      val impl = elh.embedLift(implicit lh => new DoobieImpl())
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
