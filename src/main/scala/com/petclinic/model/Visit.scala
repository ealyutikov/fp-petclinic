package com.petclinic.model

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.numeric.PosLong
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema
import java.time.LocalDate

import sttp.tapir.codec.refined._
import sttp.tapir.codec.newtype._
import io.circe.refined._

@derive(encoder, decoder, schema)
final case class Visit(
  id: Visit.Id,
  petId: Long,
  visitDate: LocalDate,
  description: String
)

object Visit {
  @derive(encoder, decoder, schema)
  @newtype final case class Id(value: PosLong)
}
