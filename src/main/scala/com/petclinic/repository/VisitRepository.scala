package com.petclinic.repository

import cats.{Apply, Monad}
import com.petclinic.logging.Logger.{log, Log}
import com.petclinic.model.{Pet, Visit}
import derevo.derive
import distage.Lifecycle
import doobie.{ConnectionIO, LogHandler}
import tofu.doobie.LiftConnectionIO
import tofu.doobie.log.EmbeddableLogHandler
import tofu.higherKind.derived.representableK
import tofu.higherKind.Mid
import tofu.syntax.monadic._
import doobie.implicits._
import doobie.refined.implicits._
import doobie.implicits.legacy.localdate._

@derive(representableK)
trait VisitRepository[F[_]] {
  def save(visit: Visit): F[Int]
  def find(id: Pet.Id): F[List[Visit]]
}

object VisitRepository {

  final class Maker[DB[_] : Monad : LiftConnectionIO : Log](implicit elh: EmbeddableLogHandler[DB])
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[DB]
      val impl = elh.embedLift(implicit lh => new DoobieImpl())
      mid attach impl
    })

  private final class DoobieImpl(implicit lh: LogHandler) extends VisitRepository[ConnectionIO] {
    override def save(visit: Visit): ConnectionIO[Int] = {
      sql"""
        |insert into visits (
        |   id, pet_id, visit_date, description
        |) values (
        |   ${visit.id}, ${visit.petId}, ${visit.visitDate}, ${visit.description}
        |)""".stripMargin.update.run
    }

    override def find(id: Pet.Id): ConnectionIO[List[Visit]] = {
      sql"""select * from visits where pet_id=$id"""
        .stripMargin
        .query[Visit]
        .to[List]
    }
  }

  final class LoggingMid[F[_] : Apply : Log] extends VisitRepository[Mid[F, *]] {
    override def save(visit: Visit): Mid[F, Int] =
      log.info(s"Started db save()") *> _ <* log.info(s"Finished db save()")

    override def find(id: Pet.Id): Mid[F, List[Visit]] =
      log.info(s"Started db find()") *> _ <* log.info(s"Finished db find()")
  }

}
