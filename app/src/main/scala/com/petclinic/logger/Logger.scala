package com.petclinic.logger

import cats.effect.Sync
import cats.syntax.option._
import com.petclinic.config.AppConfig
import izumi.fundamentals.platform.time.IzTimeSafe
import izumi.logstage.api.config.{LoggerConfig, LoggerPathConfig}
import izumi.logstage.api.rendering.json.LogstageCirceRenderingPolicy
import izumi.logstage.api.rendering.logunits.{Extractor, Renderer, Styler}
import izumi.logstage.api.rendering.{RenderingOptions, StringRenderingPolicy}
import izumi.logstage.api.routing.{ConfigurableLogRouter, LogConfigServiceImpl, StaticLogRouter}
import izumi.logstage.sink.{ConsoleSink, QueueingSink}
import logstage.{IzLogger, Level}

object Logger {

  private lazy val separator = new Extractor.Constant(" | ")

  private lazy val template = new Renderer.Aggregate(
    Seq(
      new Extractor.Constant("["),
      new Styler.LevelColor(Seq(new Extractor.Timestamp(IzTimeSafe.ISO_LOCAL_DATE_TIME_3NANO))),
      separator,
      new Styler.LevelColor(Seq(new Extractor.Level(size = 5))),
      separator,
      new Styler.Colored(Console.MAGENTA, Seq(new Styler.Compact(Seq(new Extractor.LoggerName()), takeRight = 3))),
      separator,
      new Extractor.ThreadId(),
      new Extractor.Constant(":"),
      new Extractor.ThreadName(),
      separator,
      new Extractor.LoggerContext(),
      new Extractor.Constant("]"),
      new Extractor.Constant(":"),
      Extractor.Space,
      new Extractor.Message()
    )
  )

  /** {{{ <pattern>[%date | %level | %logger | %thread | %context]: %message</pattern> }}} */
  def make[F[_] : Sync](cfg: AppConfig.LoggerConfig): F[IzLogger] = Sync[F].delay {

    val renderingPolicy =
      if (cfg.json)
        new LogstageCirceRenderingPolicy()
      else {
        val options = cfg.options.getOrElse(RenderingOptions(withExceptions = true, colored = true))
        new StringRenderingPolicy(options, template.some)
      }

    val queueingSink = new QueueingSink(new ConsoleSink(renderingPolicy))

    val sinks = Seq(queueingSink)

    val entries = cfg.levels.flatMap {
      case (levelName, paths) =>
        val level = Level.parseLetter(levelName)
        paths.map(path => (path, LoggerPathConfig(level, sinks)))
    }

    val router =
      new ConfigurableLogRouter(new LogConfigServiceImpl(LoggerConfig(LoggerPathConfig(Level.Info, sinks), entries)))

    queueingSink.start()
    StaticLogRouter.instance.setup(router)
    IzLogger(router)
  }

}
