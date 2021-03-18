package com.petclinic.util

import cats.effect.{Resource, Sync}
import cats.Applicative

import java.util.concurrent.{Executors, ExecutorService}
import scala.concurrent.ExecutionContext

object resources {

  def fixedThreadPool[F[_]](size: Int)(implicit F: Sync[F]): Resource[F, ExecutionContext] = {
    val acquire = F.delay(Executors.newFixedThreadPool(size))
    val release = (es: ExecutorService) => F.delay(es.shutdown())
    Resource.make(acquire)(release).map(ExecutionContext.fromExecutor)
  }

  def cachedThreadPool[F[_]](implicit F: Sync[F]): Resource[F, ExecutionContext] = {
    val acquire = F.delay(Executors.newCachedThreadPool)
    val release = (es: ExecutorService) => F.delay(es.shutdown())
    Resource.make(acquire)(release).map(ExecutionContext.fromExecutor)
  }

  final implicit class ToResourceOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def toResource(implicit F: Applicative[F]): Resource[F, A] = Resource.liftF(fa)
  }

}
