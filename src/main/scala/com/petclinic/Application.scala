package com.petclinic

import com.petclinic.model.AppCtx
import tofu.doobie.transactor.Txr
import tofu.zioInstances.implicits._
import zio._
import zio.interop.catz._
import zio.interop.catz.implicits._
import tofu.doobie.instances.implicits._

object Application extends App with AppF {

  type I[A] = Task[A]
  type F[A] = RIO[AppCtx, A]
  type DB[x] = Txr.Contextual[I, AppCtx]#DB[x]

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    Task.concurrentEffectWith { implicit CE =>
      runF[I, F, DB].orDie as ExitCode.success
    }

}
