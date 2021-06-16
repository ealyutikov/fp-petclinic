package com.petclinic.database

import cats.Functor
import com.petclinic.logging.Logger._
import distage.Lifecycle
import doobie.util.log.{ExecFailure, LogEvent, ProcessingFailure, Success}
import izumi.logstage.api.rendering.LogstageCodec
import tofu.doobie.log.{EmbeddableLogHandler, LogHandlerF}
import tofu.lift.{Lift, UnliftIO}
import tofu.syntax.lift._

object DoobieLogHandler {

  final class Maker[F[_] : Functor : UnliftIO : Log, DB[_] : Lift[F, *[_]]]
    extends Lifecycle.Of(Lifecycle.pure(makeEmbeddable[F, DB]))

  def makeEmbeddable[F[_] : Functor : UnliftIO : Log, DB[_] : Lift[F, *[_]]]: EmbeddableLogHandler[DB] =
    EmbeddableLogHandler.async(handleEventWithLogging).lift[DB]

  private def handleEventWithLogging[F[_] : Log]: LogHandlerF[F] = {
    case ev @ (_: Success) => log.info(s"Successful statement execution ${ev -> "sql-event"}")
    case ev @ ProcessingFailure(_, _, _, _, error) =>
      log.error(s"Failed ResultSet processing ${ev -> "sql-event"} $error")
    case ev @ ExecFailure(_, _, _, error) => log.error(s"Failed statement execution ${ev -> "sql-event"} $error")
  }

  private def fmt(s: String): String = s.linesIterator.dropWhile(_.trim.isEmpty).mkString(" ")
  private def asStrings(a: List[Any]): List[String] = a.map(_.toString)

  implicit val logEventLogstageCodec: LogstageCodec[LogEvent] = (writer, ev) => {
    def writeElem[V : LogstageCodec](k: String, v: V): Unit = {
      writer.nextMapElementOpen()
      LogstageCodec[String].write(writer, k)
      writer.mapElementSplitter()
      LogstageCodec[V].write(writer, v)
      writer.nextMapElementClose()
    }

    writer.openMap()
    val (s, a, e) = ev match {
      case Success(s, a, e1, e2) =>
        writeElem("sql-event-type", "Success")
        writeElem("sql-processing-ms", e2.toMillis)
        writeElem("sql-total-ms", (e1 + e2).toMillis)
        (s, a, e1.toMillis)
      case ProcessingFailure(s, a, e1, e2, _) =>
        writeElem("sql-event-type", "ProcessingFailure")
        writeElem("sql-processing-ms", e2.toMillis)
        writeElem("sql-total-ms", (e1 + e2).toMillis)
        (s, a, e1.toMillis)
      case ExecFailure(s, a, e1, _) =>
        writeElem("sql-event-type", "ExecFailure")
        (s, a, e1.toMillis)
    }
    writeElem("sql-statement", fmt(s))
    writeElem("sql-args", asStrings(a))
    writeElem("sql-exec-ms", e)
    writer.closeMap()
  }

}
