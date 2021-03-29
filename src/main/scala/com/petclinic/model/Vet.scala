package com.petclinic.model

import derevo.circe.magnolia.{decoder, encoder}
import derevo.derive
import doobie.Meta
import doobie.refined.implicits._
import eu.timepit.refined.types.numeric.PosLong
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema
import sttp.tapir.codec.refined._
import io.circe.refined._
import sttp.tapir.codec.newtype._

@derive(encoder, decoder, schema)
final case class Vet(id: Vet.Id, firstName: NonBlankString, lastName: NonBlankString)

object Vet {

  @derive(encoder, decoder)
  @newtype final case class Id(value: PosLong)

  object Id {
    implicit val meta: Meta[Id] = deriving
  }

}
