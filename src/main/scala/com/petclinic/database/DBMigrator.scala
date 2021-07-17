package com.petclinic.database

import cats.{Applicative, Apply}
import cats.effect.Sync
import com.petclinic.config.{DbConfig, FlywayConfig}
import com.petclinic.logging.Logger.{log, Log}
import derevo.derive
import derevo.tagless.applyK
import izumi.distage.model.definition.Lifecycle
import org.flywaydb.core.{Flyway => JFlyway}
import tofu.Blocks
import tofu.higherKind.Mid
import tofu.syntax.monadic._
import cats.tagless.syntax.functorK._

@derive(applyK)
trait DBMigrator[F[_]] {
  def clean: F[Unit]
  def migrate: F[Unit]
  def refresh: F[Unit]
}

object DBMigrator {
  final class Maker[I[_] : Sync : Log : Blocks](config: FlywayConfig, db: DbConfig)
    extends Lifecycle.Of(Lifecycle.pure(configure[I](config, db)))

  private final class Impl[I[_] : Log](jFlyway: JFlyway)(implicit F: Sync[I]) extends DBMigrator[I] {
    def clean: I[Unit] = F.delay(jFlyway.clean()).void
    def migrate: I[Unit] = F.delay(jFlyway.migrate()) >>= (r => log.info(s"${r.migrationsExecuted} migrations applied"))
    def refresh: I[Unit] = clean *> migrate
  }

  private final class LoggingMid[I[_] : Apply : Log] extends DBMigrator[Mid[I, *]] {
    def clean: Mid[I, Unit] = log.info("Start database cleanup") *> _ <* log.info("Database cleanup finished")
    def migrate: Mid[I, Unit] = log.info("Start database migration") *> _ <* log.info("Database migration finished")
    def refresh: Mid[I, Unit] = log.info("Start database refresh") *> _ <* log.info("Database refresh finished")
  }

  private final class NoopImpl[I[_] : Applicative] extends DBMigrator[I] {
    def clean: I[Unit] = unit[I]
    def migrate: I[Unit] = unit[I]
    def refresh: I[Unit] = unit[I]
  }

  private def configure[I[_] : Sync : Log : Blocks](config: FlywayConfig, db: DbConfig): DBMigrator[I] =
    if (config.enableMigrations) {
      val flyway = JFlyway
        .configure()
        .dataSource(db.connection.url, db.connection.user, db.connection.pass)
        .locations(config.locations: _*)
        .load()
      new LoggingMid[I] attach (new Impl(flyway): DBMigrator[I]).mapK(Blocks[I].funK)
    } else new NoopImpl[I]

}
