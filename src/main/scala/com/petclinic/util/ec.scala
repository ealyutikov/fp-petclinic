package com.petclinic.util

import cats.effect.{Blocker, Resource, Sync}
import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.ExecutionContext

object ec {

  def blocker[F[_] : Sync](name: String): Resource[F, Blocker] =
    ec.cachedThreadPool[F](Option(ec.namedThreadFactory(name))).map(Blocker.liftExecutionContext)

  def fixedThreadPool[F[_] : Sync](
    size: Int,
    threadFactory: Option[ThreadFactory] = None
  ): Resource[F, ExecutionContext] =
    makeThreadPool[F] {
      threadFactory.fold(Executors.newFixedThreadPool(size))(Executors.newFixedThreadPool(size, _))
    }

  def cachedThreadPool[F[_] : Sync](threadFactory: Option[ThreadFactory] = None): Resource[F, ExecutionContext] =
    makeThreadPool[F] {
      threadFactory.fold(Executors.newCachedThreadPool)(Executors.newCachedThreadPool)
    }

  private def makeThreadPool[F[_]](
    unsafeMakeES: => ExecutorService
  )(implicit S: Sync[F]): Resource[F, ExecutionContext] = {
    val alloc = S.delay(unsafeMakeES)
    val free = (es: ExecutorService) => S.delay(es.shutdown())
    Resource.make(alloc)(free).map(ExecutionContext.fromExecutor)
  }

  def namedThreadFactory(name: String, daemon: Boolean = false): ThreadFactory =
    new ThreadFactory {
      private val parentGroup = Option(System.getSecurityManager)
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
