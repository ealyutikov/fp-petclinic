package com.petclinic.util

import cats.Applicative
import cats.effect.Resource

object resource {
  final implicit class ToResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    @inline def toResource(implicit F: Applicative[F]): Resource[F, A] = Resource.eval(fa)
  }
}
