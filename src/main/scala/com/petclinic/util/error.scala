package com.petclinic.util

import cats.effect.ApplicativeThrow
import com.petclinic.cotroller.Controller.ErrorResponse
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.functor._

object error {
  final implicit class ToErrorOps[F[_], A](private val fa: F[A]) extends AnyVal {
    @inline def toError(implicit F: ApplicativeThrow[F]): F[Either[ErrorResponse, A]] =
      fa.attempt.map(_.leftMap(error => ErrorResponse(error.getMessage)))
  }
}
