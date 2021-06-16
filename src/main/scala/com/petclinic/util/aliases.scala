package com.petclinic.util

import cats.data.NonEmptyList
import cats.~>
import doobie.ConnectionIO
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.string.Trimmed

object aliases {
  type Trans[F[_]] = ConnectionIO ~> F

  type Nel[A] = NonEmptyList[A]
  val Nel: NonEmptyList.type = NonEmptyList

  type NonBlank = NonEmpty And Trimmed
  type NonBlankString = String Refined NonBlank
}
