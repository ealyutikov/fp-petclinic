package com.petclinic.model

import com.petclinic.util.aliases._
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.numeric.PosLong
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

import sttp.tapir.codec.refined._
import sttp.tapir.codec.newtype._
import io.circe.refined._

@derive(encoder, decoder, schema)
final case class Vet(
  id: Vet.Id,
  firstName: NonBlankString,
  lastName: NonBlankString,
  specialty: List[Specialty]
)

object Vet {

  def apply(lite: LiteVet, specialty: List[Specialty]): Vet =
    Vet(lite.id, lite.firstName, lite.lastName, specialty)

  @derive(encoder, decoder, schema)
  @newtype final case class Id(value: PosLong)

  final case class LiteVet(id: Vet.Id, firstName: NonBlankString, lastName: NonBlankString)

}
