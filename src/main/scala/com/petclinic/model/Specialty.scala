package com.petclinic.model

import com.petclinic.util.aliases.NonBlankString
import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.types.numeric.PosLong
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

import sttp.tapir.codec.refined._
import sttp.tapir.codec.newtype._
import io.circe.refined._

@derive(encoder, decoder, schema)
final case class Specialty(id: Specialty.Id, name: NonBlankString)

object Specialty {
  @derive(encoder, decoder, schema)
  @newtype final case class Id(value: PosLong)
}
