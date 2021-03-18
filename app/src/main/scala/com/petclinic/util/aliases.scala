package com.petclinic.util

import cats.data.NonEmptyList
import cats.~>
import doobie.free.connection.ConnectionIO

object aliases {

  type Nel[A] = NonEmptyList[A]
  val Nel: NonEmptyList.type = NonEmptyList

  type Trans[F[_]] = ConnectionIO ~> F

}
