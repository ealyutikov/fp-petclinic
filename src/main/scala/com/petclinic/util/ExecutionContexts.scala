package com.petclinic.util

import cats.effect.{Blocker, Resource, Sync}

import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext

object ExecutionContexts {

  def blocker[F[_] : Sync](name: String): Resource[F, Blocker] =
    cachedThreadPool[F](name)
      .map(Blocker.liftExecutionContext)

  def fixedThreadPool[F[_] : Sync](size: Int, name: String): Resource[F, ExecutionContext] =
    makeThreadPool[F](Executors.newFixedThreadPool(size, ExecutionContexts.namedThreadFactory(name)))

  def cachedThreadPool[F[_] : Sync](name: String): Resource[F, ExecutionContext] =
    makeThreadPool[F](Executors.newCachedThreadPool(ExecutionContexts.namedThreadFactory(name)))

  private def makeThreadPool[F[_]](
    unsafeMakeES: => ExecutorService
  )(implicit S: Sync[F]): Resource[F, ExecutionContext] = {
    val alloc = S.delay(unsafeMakeES)
    val free = (es: ExecutorService) => S.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)
  }

  private def namedThreadFactory(name: String, daemon: Boolean = false): ThreadFactory =
    new ThreadFactory {

      private val parentGroup =
        Option(System.getSecurityManager)
          .fold(Thread.currentThread().getThreadGroup)(_.getThreadGroup)

      private val threadGroup = new ThreadGroup(parentGroup, name)
      private val threadCount = new AtomicInteger(1)
      private val threadHash = Integer.toUnsignedString(this.hashCode())

      override def newThread(r: Runnable): Thread = {
        val newThreadNumber = threadCount.getAndIncrement()
        val thread = new Thread(threadGroup, r)
        thread.setName(s"$name-$newThreadNumber-$threadHash")
        thread.setDaemon(daemon)
        thread
      }

    }

}
