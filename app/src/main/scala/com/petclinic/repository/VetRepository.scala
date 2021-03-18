package com.petclinic.repository

import com.petclinic.model.Vet
import com.petclinic.util.aliases.Trans
import derevo.derive
import derevo.tagless.functorK
import distage.Lifecycle
import doobie.implicits._
import doobie.{ConnectionIO, LogHandler}
import doobie.refined.implicits._
import cats.tagless.syntax.functorK._

@derive(functorK)
trait VetRepository[DB[_]] {
  def findAll(): DB[List[Vet]]
}

object VetRepository {

  final class Maker[F[_]](xa: Trans[F], lh: LogHandler)
    extends Lifecycle.Of(Lifecycle.pure(new DoobieImpl()(lh).mapK(xa)))

  private final class DoobieImpl(implicit lh: LogHandler) extends VetRepository[ConnectionIO] {

    override def findAll(): ConnectionIO[List[Vet]] =
      sql"""SELECT * FROM vets;"""
        .stripMargin
        .query[Vet]
        .to[List]

  }

}
