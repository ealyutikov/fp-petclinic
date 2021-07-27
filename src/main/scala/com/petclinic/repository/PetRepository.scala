package com.petclinic.repository

import cats.{Apply, Monad}
import com.petclinic.logging.Logger.{log, Log}
import com.petclinic.model.{Pet, PetType}
import derevo.derive
import distage.Lifecycle
import doobie.{ConnectionIO, LogHandler}
import tofu.doobie.log.EmbeddableLogHandler
import tofu.doobie.LiftConnectionIO
import tofu.higherKind.Mid
import tofu.higherKind.derived.representableK
import tofu.syntax.monadic._
import doobie.implicits._

import doobie.refined.implicits._
import doobie.implicits.legacy.localdate._

@derive(representableK)
trait PetRepository[F[_]] {
  def findById(id: Pet.Id): F[Option[Pet]]
  def findPetTypes(): F[List[PetType]]
  def save(pet: Pet): F[Int]
}

object PetRepository {

  final class Maker[DB[_] : Monad : LiftConnectionIO : Log](implicit elh: EmbeddableLogHandler[DB])
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[DB]
      val impl = elh.embedLift(implicit lh => new DoobieImpl())
      mid attach impl
    })

  private final class DoobieImpl(implicit lh: LogHandler) extends PetRepository[ConnectionIO] {
    override def findById(id: Pet.Id): ConnectionIO[Option[Pet]] =
      sql"""select * from pets where id=$id""".stripMargin.query[Pet].option

    override def findPetTypes(): ConnectionIO[List[PetType]] =
      sql"""select * from types"""
        .stripMargin
        .query[PetType]
        .to[List]

    override def save(pet: Pet): ConnectionIO[Int] = {
      sql"""
        |insert into pets (
        |   id, name, birth_date, type_id, owner_id
        |) values (
        |   ${pet.id}, ${pet.name}, ${pet.birthDate}, ${pet.typeId}, ${pet.ownerId}
        |)""".stripMargin.update.run
    }

  }

  private final class LoggingMid[F[_] : Apply : Log] extends PetRepository[Mid[F, *]] {
    override def findById(id: Pet.Id): Mid[F, Option[Pet]] =
      log.info(s"Started db findById()") *> _ <* log.info(s"Finished db findById()")

    override def findPetTypes(): Mid[F, List[PetType]] =
      log.info(s"Started db findPetTypes()") *> _ <* log.info(s"Finished db findPetTypes()")

    override def save(pet: Pet): Mid[F, Int] =
      log.info(s"Started db save()") *> _ <* log.info(s"Finished db save()")
  }

}
