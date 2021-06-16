package com.petclinic

import cats.Eq
import doobie.{Put, Read}
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.Coercible

package object model {
  implicit def newTypeEq[R, N](implicit ev: Coercible[Eq[R], Eq[N]], R: Eq[R]): Eq[N] = ev(R)

  implicit def newTypePut[R, N](implicit ev: Coercible[Put[R], Put[N]], R: Put[R]): Put[N] = ev(R)
  implicit def newTypeRead[R, N](implicit ev: Coercible[Read[R], Read[N]], R: Read[R]): Read[N] = ev(R)

  implicit def newTypeDecoder[A, B](implicit ev: Coercible[Decoder[A], Decoder[B]], R: Decoder[A]): Decoder[B] = ev(R)
  implicit def newTypeEncoder[A, B](implicit ev: Coercible[Encoder[A], Encoder[B]], R: Encoder[A]): Encoder[B] = ev(R)
}
