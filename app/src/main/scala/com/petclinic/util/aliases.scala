package com.petclinic.util

import cats.data.NonEmptyList
import cats.~>
import doobie.free.connection.ConnectionIO

object aliases {

  type Nel[A] = NonEmptyList[A]

  type Trans[F[_]] = ConnectionIO ~> F

}
