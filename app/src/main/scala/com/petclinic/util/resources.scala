package com.petclinic.util

import java.util.concurrent.{ExecutorService, Executors}
import cats.effect.{Resource, Sync}
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

}
