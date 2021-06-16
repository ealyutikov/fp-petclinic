package com.petclinic.util

import cats.Applicative
import cats.effect.Resource
import tofu.higherKind.{Function2K, Mid, MonoidalK}
import tofu.syntax.monoidalK._

object syntax {

  final implicit class ResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    @inline def toResource(implicit F: Applicative[F]): Resource[F, A] = Resource.eval(fa)
  }

  implicit class MidOps[U[_[_]], F[_]](private val x: U[Mid[F, *]]) extends AnyVal {
    @inline def >>>(y: U[Mid[F, *]])(implicit U: MonoidalK[U]): U[Mid[F, *]] = (x zipWithKTo y)(Function2K(_ andThen _))
  }

}
