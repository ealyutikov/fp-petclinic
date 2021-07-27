package com.petclinic.repository

import distage.{ModuleDef, TagK}

final class RepositoryModule[DB[_] : TagK] extends ModuleDef {
  make[VetRepository[DB]].fromResource[VetRepository.Maker[DB]]
  make[VisitRepository[DB]].fromResource[VisitRepository.Maker[DB]]
  make[PetRepository[DB]].fromResource[PetRepository.Maker[DB]]
}
