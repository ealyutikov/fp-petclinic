package com.petclinic.service

import com.petclinic.repository.VetRepository
import distage.{ModuleDef, Tag, TagK}
import tofu.doobie.transactor.Txr
import cats.tagless.syntax.functorK._

final class ServiceModule[F[_] : TagK, DB[_] : TagK](implicit T: Tag[Txr.Aux[F, DB]]) extends ModuleDef {
  make[VetRepository[F]].from((r: VetRepository[DB], txr: Txr.Aux[F, DB]) => r.mapK(txr.trans))

  make[VetService[F]].fromResource[VetService.Maker[F]]
}
