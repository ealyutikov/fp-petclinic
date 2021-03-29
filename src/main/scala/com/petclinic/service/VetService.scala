package com.petclinic.service

import cats.Apply
import com.petclinic.logger.Logger.{log, Log}
import com.petclinic.model.Vet
import com.petclinic.repository.VetRepository
import derevo.derive
import distage.Lifecycle
import tofu.higherKind.Mid
import tofu.higherKind.derived.representableK
import tofu.syntax.monadic._

@derive(representableK)
trait VetService[F[_]] {
  def findAll(): F[List[Vet]]
}

object VetService {

  final class Maker[F[_] : Apply : Log](repository: VetRepository[F])
    extends Lifecycle.Of(Lifecycle.pure {
      val mid = new LoggingMid[F]
      val impl = new Impl[F](repository)
      mid attach impl
    })

  private final class Impl[F[_]](repository: VetRepository[F]) extends VetService[F] {
    override def findAll(): F[List[Vet]] = repository.findAll()
  }

  final class LoggingMid[F[_] : Apply : Log] extends VetService[Mid[F, *]] {
    override def findAll(): Mid[F, List[Vet]] =
      log.info(s"Started service processing") *> _ <* log.info(s"Finished service processing")
  }

}
