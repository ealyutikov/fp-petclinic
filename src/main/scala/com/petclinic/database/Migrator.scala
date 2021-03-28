package com.petclinic.database

import cats.effect.Sync
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.petclinic.config.AppConfig.{DbConfig, FlywayConfig}
import com.petclinic.logger.Logger.{log, Log}
import distage.Lifecycle
import org.flywaydb.core.{Flyway => JFlyway}

trait Migrator[F[_]] {
  def clean: F[Unit]
  def migrate: F[Unit]
  def refresh: F[Unit]
}

object Migrator {

  final class Maker[F[_] : Sync : Log](dbConfig: DbConfig, flywayConfig: FlywayConfig)
    extends Lifecycle.LiftF(make[F](dbConfig, flywayConfig))

  private def make[F[_] : Sync : Log](dbConfig: DbConfig, flywayConfig: FlywayConfig): F[Migrator[F]] =
    for {
      _ <- log.info(s"Creating flyway")
      flyway <- Sync[F].delay(
        JFlyway.configure
          .dataSource(dbConfig.url, dbConfig.user, dbConfig.pass)
          .locations(flywayConfig.location)
          .load
      )
    } yield new Impl[F](flywayConfig, flyway)

  // internal

  private final class Impl[F[_] : Log](config: FlywayConfig, flyway: JFlyway)(implicit F: Sync[F]) extends Migrator[F] {

    def clean: F[Unit] = for {
      _ <- log.info("Clean database: start")
      _ <- F.delay(flyway.clean())
      _ <- log.info(s"Clean database: done")
    } yield ()

    def migrate: F[Unit] =
      if (!config.enableMigrations) log.info("Skip migration")
      else
        for {
          _ <- log.info("Migrate database: start")
          _ <- F.delay(flyway.migrate())
          _ <- log.info(s"Migrate database: done")
        } yield ()

    def refresh: F[Unit] = clean *> migrate

  }

}
