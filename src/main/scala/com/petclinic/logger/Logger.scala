package com.petclinic.logger

import cats.effect.{Resource, Sync}
import cats.syntax.option._
import com.petclinic.config.LogConfig
import com.petclinic.config.LogConfig.Paths
import com.petclinic.util.syntax._
import distage.Lifecycle
import izumi.fundamentals.platform.time.IzTimeSafe
import izumi.logstage.api.{Log => ApiLog}
import izumi.logstage.api.rendering.logunits._
import izumi.logstage.api.Log.{CustomContext, Level}
import izumi.logstage.api.config.{LoggerConfig, LoggerPathConfig}
import izumi.logstage.api.rendering.{RenderingOptions, StringRenderingPolicy}
import izumi.logstage.api.rendering.json.LogstageCirceRenderingPolicy
import izumi.logstage.api.rendering.StrictEncoded._
import izumi.logstage.api.routing.{ConfigurableLogRouter, LogConfigServiceImpl, StaticLogRouter}
import izumi.logstage.sink.{ConsoleSink, QueueingSink}
import logstage.strict.{LogIOStrict => LogIO}
import logstage.IzLogger

object Logger {

  type Log[F[_]] = LogIO[F]
  val Log: LogIO.type = LogIO

  def log[F[_]](implicit L: Log[F]): L.type = L

  type LogCtx = CustomContext
  val LogCtx: CustomContext.type = CustomContext

  val mapToLogCtx: Map[String, String] => LogCtx = logMap => {
    LogCtx.fromMap(logMap.view.mapValues(to(_)).toMap)
  }

  final class Maker[F[_] : Sync](config: LogConfig) extends Lifecycle.OfCats(makeResource(config))

  private def makeResource[F[_]](config: LogConfig)(implicit F: Sync[F]): Resource[F, IzLogger] = {
    val renderingPolicy =
      if (config.json) new LogstageCirceRenderingPolicy()
      else {
        val options = config.options.getOrElse(RenderingOptions(withExceptions = false, colored = true))
        val template = stringRendererTemplate
        new StringRenderingPolicy(options, template.some)
      }

    for {
      consoleSink  <- Resource.fromAutoCloseable(F.delay(new ConsoleSink(renderingPolicy)))
      queueingSink <- Resource.fromAutoCloseable(F.delay(new QueueingSink(consoleSink)))
      sinks = Seq(queueingSink)
      levels = {
        import shapeless.syntax.std.product._
        config.levels.toMap[Symbol, Paths].flatMap { case (symbolLevel, packs) =>
          val level = Level.parseLetter(symbolLevel.name)
          packs.orEmpty.map(_ -> LoggerPathConfig(level, sinks))
        }
      }
      threshold = Level.Info
      logConfig = LoggerConfig(LoggerPathConfig(threshold, sinks), levels)
      logConfigService <- Resource.fromAutoCloseable(F.delay(new LogConfigServiceImpl(logConfig)))
      router           <- Resource.fromAutoCloseable(F.delay(new ConfigurableLogRouter(logConfigService)))
      _                <- F.delay(queueingSink.start()).toResource
      _                <- F.delay(StaticLogRouter.instance.setup(router)).toResource
    } yield IzLogger(router)

  }

  private val loggerContextExtractor: Extractor = (entry: ApiLog.Entry, context: RenderingOptions) => {
    val values = entry.context.customContext.values
    val out =
      if (values.nonEmpty) values
        .map { v =>
          LogFormat.Default.formatKv(context.colored)(v.name, v.codec, v.value)
        }
        .mkString("[", ", ", "]")
      else ""
    LETree.TextNode(out)
  }

  private val stringRendererTemplate: Renderer.Aggregate = new Renderer.Aggregate(
    Seq(
      new Extractor.Constant("["),
      new Extractor.Timestamp(IzTimeSafe.ISO_LOCAL_DATE_TIME_3NANO),
      Extractor.Space,
      new Styler.LevelColor(Seq(new Extractor.Constant("["), new Extractor.Level(5), new Extractor.Constant("]"))),
      Extractor.Space,
      new Extractor.Constant("from"),
      Extractor.Space,
      new Extractor.LoggerName(),
      Extractor.Space,
      new Extractor.Constant("in"),
      Extractor.Space,
      new Extractor.ThreadName(),
      new Extractor.Constant("]"),
      Extractor.Space,
      new Extractor.Message(),
      Extractor.Space,
      loggerContextExtractor
    )
  )

}
