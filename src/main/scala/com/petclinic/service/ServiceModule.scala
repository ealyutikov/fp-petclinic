package com.petclinic.service

import com.petclinic.repository._
import distage.{ModuleDef, TagK}
import tofu.doobie.transactor.Txr
import cats.tagless.syntax.functorK._

final class ServiceModule[F[_] : TagK, DB[_] : TagK] extends ModuleDef {
  make[VetRepository[F]].from((r: VetRepository[DB], txr: Txr.Aux[F, DB]) => r.mapK(txr.trans))
  make[VisitRepository[F]].from((r: VisitRepository[DB], txr: Txr.Aux[F, DB]) => r.mapK(txr.trans))

  make[VetService[F]].fromResource[VetService.Maker[F]]
}
