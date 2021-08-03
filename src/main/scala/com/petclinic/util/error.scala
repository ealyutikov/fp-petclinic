package com.petclinic.util

import cats.effect.ApplicativeThrow
import cats.syntax.applicativeError._
import cats.syntax.either._
import cats.syntax.functor._
import com.petclinic.error.HttpError
import com.petclinic.error.HttpError.{Internal, NoContent}
import mouse.boolean._

object error {

  final implicit class ToErrorOps[F[_], A](private val fa: F[A]) extends AnyVal {
    @inline def toError(implicit F: ApplicativeThrow[F]): F[Either[HttpError, A]] =
      fa.attempt.map(_.leftMap(error => Internal(error.getMessage)))
  }

  final implicit class ListToErrorOps[F[_], A](private val fa: F[List[A]]) extends AnyVal {
    @inline def listToError(implicit F: ApplicativeThrow[F]): F[Either[HttpError, List[A]]] =
      fa.attempt.map(
          _.fold(
            error => Internal(error.getMessage).asLeft,
            success => success.isEmpty.fold(NoContent.asLeft, success.asRight)
          )
        )
  }

}
