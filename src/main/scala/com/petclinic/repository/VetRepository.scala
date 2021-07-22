package com.petclinic.repository

import cats.{Apply, Monad}
import com.petclinic.logging.Logger.{log, Log}
import com.petclinic.model.Specialty
import com.petclinic.model.Vet.LiteVet
import com.petclinic.util.list._
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
  def findWithSpecialty(): DB[Map[LiteVet, List[Specialty]]]
}

object VetRepository {

  final class Maker[DB[_] : Monad : LiftConnectionIO : Log](implicit elh: EmbeddableLogHandler[DB])
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[DB]
      val impl = elh.embedLift(implicit lh => new DoobieImpl())
      mid attach impl
    })

  private final class DoobieImpl(implicit lh: LogHandler) extends VetRepository[ConnectionIO] {
    override def findWithSpecialty(): ConnectionIO[Map[LiteVet, List[Specialty]]] = {
      sql"""select v.id, v.first_name, v.last_name, s.id, s."name" from vets v
        |inner join vet_specialties vs
        |on v.id  = vs.vet_id
        |inner join specialties s
        |on s.id = vs.specialty_id"""
        .stripMargin
        .query[(LiteVet, Specialty)]
        .to[List]
    }.map(_.toMapList)
  }

  final class LoggingMid[F[_] : Apply : Log] extends VetRepository[Mid[F, *]] {
    override def findWithSpecialty(): Mid[F, Map[LiteVet, List[Specialty]]] =
      log.info(s"Started db processing") *> _ <* log.info(s"Finished db processing")
  }

}
