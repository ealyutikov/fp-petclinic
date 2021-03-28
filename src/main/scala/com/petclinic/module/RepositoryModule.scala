package com.petclinic.module

import com.petclinic.repository.VetRepository
import distage.{ModuleDef, TagK}

final class RepositoryModule[F[_] : TagK] extends ModuleDef {

  make[VetRepository[F]].fromResource[VetRepository.Maker[F]]

}
