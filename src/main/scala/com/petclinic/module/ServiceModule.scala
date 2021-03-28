package com.petclinic.module

import com.petclinic.service.VetService
import distage.{ModuleDef, TagK}

final class ServiceModule[F[_] : TagK] extends ModuleDef {

  make[VetService[F]].fromResource[VetService.Maker[F]]

}
