package com.petclinic.module

import com.petclinic.repository.VetRepository
import com.petclinic.util.aliases.Trans
import distage.{ModuleDef, TagK}

final class RepositoryModule[DB[_] : TagK] extends ModuleDef {

  make[VetRepository[DB]].from(VetRepository[DB](_: Trans[DB]))

}
