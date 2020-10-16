package com.petclinic.database

import cats.effect.Sync
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import logstage.LogIO
import logstage.LogIO.log
import org.flywaydb.core.{Flyway => JFlyway}

trait Migrator[F[_]] {
  def clean: F[Unit]
  def migrate: F[Unit]
  def refresh: F[Unit]
}

object Migrator {

  def apply[F[_] : Sync : LogIO](config: MigrationConfig): F[Migrator[F]] =
    for {
      _ <- log.info(s"Creating flyway")
      flyway <- Sync[F].delay(
        JFlyway.configure
          .dataSource(config.url, config.user, config.pass)
          .locations(config.locations: _*)
          .load
      )
    } yield new Impl[F](config, flyway)

  final case class MigrationConfig(
    enableMigrations: Boolean,
    url: String,
    user: String,
    pass: String,
    locations: Seq[String]
  )

  // internal

  private final class Impl[F[_] : LogIO](config: MigrationConfig, flyway: JFlyway)(implicit F: Sync[F])
    extends Migrator[F] {

    def clean: F[Unit] = for {
      _      <- log.info("Clean database: start")
      result <- F.delay(flyway.clean())
      _      <- log.info(s"Clean database: done with ${result.warnings}")
    } yield ()

    def migrate: F[Unit] =
      if (!config.enableMigrations) log.info("Skip migration")
      else
        for {
          _      <- log.info("Migrate database: start")
          result <- F.delay(flyway.migrate())
          _      <- log.info(s"Migrate database: done with ${result.warnings} ${result.migrationsExecuted}")
        } yield ()

    def refresh: F[Unit] = clean *> migrate

  }

}
