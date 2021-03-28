package com.petclinic.module

import cats.Monad
import cats.data.NonEmptyList
import com.petclinic.http.{BuildInfoController, Controller, VetController}
import distage.{ModuleDef, TagK}

final class ControllersModule[F[_] : TagK] extends ModuleDef {

  many[Controller[F]].addResource[BuildInfoController.Maker[F]]

  many[Controller[F]].addResource[VetController.Maker[F]]

  make[Controller[F]].named("controllers").from { (F: Monad[F], controllers: Set[Controller[F]]) =>
    NonEmptyList.fromListUnsafe(controllers.toList).reduce(Controller.catsSemigroupForRoutes(F))
  }

}
