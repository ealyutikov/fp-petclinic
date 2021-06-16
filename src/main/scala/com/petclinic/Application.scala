package com.petclinic

import com.petclinic.ctx._
import tofu.zioInstances.implicits._
import zio._
import zio.internal.Platform
import zio.interop.catz._
import zio.interop.catz.implicits._

object Application extends zio.App with AppF {

  type F[A] = RIO[Ctx, A]

  implicit val R = Runtime(EmptyCtx, Platform.default)

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    runF[RIO[Ctx, *]].provide(EmptyCtx).orDie.exitCode

}
