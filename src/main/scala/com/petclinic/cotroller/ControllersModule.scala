package com.petclinic.cotroller

import cats.Monad
import cats.data.NonEmptyList
import com.petclinic.Application.F
import distage.{Id, ModuleDef, TagK}
import sttp.tapir.Endpoint

final class ControllersModule[I[_] : TagK] extends ModuleDef {
  make[SwaggerController[I]].fromResource[SwaggerController.Maker[I]]
  many[Controller[I]].addResource[BuildInfoController.Maker[I]]


  many[Controller[I]].addResource[VetController.Maker[I, F]]

  make[List[Endpoint[_, _, _, _]]].from { (controller: Controller[I] @Id("controllers")) =>
    controller.endpoints.toList
  }

  make[Controller[I]].named("controllers").from { (F: Monad[I], controllers: Set[Controller[I]]) =>
    NonEmptyList.fromListUnsafe(controllers.toList).reduce(Controller.catsSemigroupForRoutes(F))
  }

}
