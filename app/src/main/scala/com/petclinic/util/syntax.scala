package com.petclinic.util

import cats.Applicative
import cats.effect.Resource

object syntax {

  final implicit class ResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def toResource(implicit F: Applicative[F]): Resource[F, A] = Resource.liftF(fa)
  }

//  implicit class ToResponseOps[F[_] : MonadThrow, A](resultF: F[A]) {
//    def toResponse(handler: Throwable => ErrorType): F[Either[ErrorType, A]] =
//      resultF.attempt.map(_.leftMap(handler))
//  }

}
