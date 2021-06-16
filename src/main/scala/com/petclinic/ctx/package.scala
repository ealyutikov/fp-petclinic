package com.petclinic

import tofu.{WithContext, WithLocal}

package object ctx {

  type Ctx = Map[String, String]
  val EmptyCtx = Map.empty[String, String]

  type AskCtx[F[_]] = F WithContext Ctx
  type LocalCtx[F[_]] = F WithLocal Ctx

}
