package com.petclinic.repository

import com.petclinic.model.{NonBlankString, Vet}
import com.petclinic.util.aliases.Trans
import doobie.refined.implicits._
import doobie.implicits._
import eu.timepit.refined.types.numeric.PosLong
import mouse.all._

trait VetRepository[DB[_]] {
  def findAll(): DB[List[Vet]]
}

object VetRepository {

  def apply[DB[_] : Trans]: VetRepository[DB] = new Impl[DB]

  private final class Impl[DB[_]](implicit xa: Trans[DB]) extends VetRepository[DB] {
    override def findAll(): DB[List[Vet]] =
      sql"""SELECT
           |    v.id, v.first_name, v.last_name, s.id, s.name
           |FROM
           |    vets v
           |JOIN
           |    vet_specialties vs
           |ON
           |    vs.vet_id = v.id
           |JOIN
           |    specialties s
           |ON
           |    vs.specialty_id = s.id""".stripMargin
        .query[(PosLong, NonBlankString, NonBlankString, PosLong, NonBlankString)]
        .to[List]
        .map { list => ???
        } ||> xa
  }

}
