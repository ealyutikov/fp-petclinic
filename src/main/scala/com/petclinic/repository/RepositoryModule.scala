package com.petclinic.repository

import distage.{ModuleDef, TagK}

final class RepositoryModule[DB[_] : TagK] extends ModuleDef {
  make[VetRepository[DB]].fromResource[VetRepository.Maker[DB]]
}
