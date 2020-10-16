package com.petclinic.util

import cats.arrow.FunctionK
import cats.data.NonEmptyList
import cats.~>
import doobie.free.connection.ConnectionIO

object aliases {

  implicit def idLift[F[_]]: FunctionK[F, F] = FunctionK.id[F]

  type Nel[A] = NonEmptyList[A]

  type Trans[F[_]] = ConnectionIO ~> F

}
