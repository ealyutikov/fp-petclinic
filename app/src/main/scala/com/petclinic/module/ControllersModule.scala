package com.petclinic.module

import cats.Monad
import cats.data.NonEmptyList
import cats.effect.{ContextShift, Sync}
import com.petclinic.controller.{BuildInfoController, Controller}
import distage.{ModuleDef, TagK}

final class ControllersModule[F[_] : TagK] extends ModuleDef {

  many[Controller[F]].add(BuildInfoController[F]()(_: Sync[F], _: ContextShift[F]))

  make[Controller[F]].named("controllers").from { (F: Monad[F], controllers: Set[Controller[F]]) =>
    NonEmptyList.fromListUnsafe(controllers.toList).reduce(Controller.catsSemigroupForRoutes(F))
  }

}
