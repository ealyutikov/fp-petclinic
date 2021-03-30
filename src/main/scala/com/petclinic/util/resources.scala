package com.petclinic.util

import cats.effect.Resource
import cats.Applicative

object resources {

  final implicit class ToResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def toResource(implicit F: Applicative[F]): Resource[F, A] = Resource.liftF(fa)
  }

}
