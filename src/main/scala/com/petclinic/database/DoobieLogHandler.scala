package com.petclinic.database

import doobie.LogHandler
import doobie.util.log.{ExecFailure, LogEvent, ProcessingFailure, Success}
import org.slf4j.{LoggerFactory, Logger => JLogger}

object DoobieLogHandler {

  def make(): LogHandler = LogHandler(logDoobieEvent(LoggerFactory.getLogger(getClass))(_))

  private def logDoobieEvent(logger: JLogger): LogEvent => Unit = {
    case Success(s, a, e1, e2) =>
      logger.trace {
        s"""Successful Statement Execution:
          |
          |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n")}
          |
          | arguments = [${a.mkString(", ")}]
          |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
         """.stripMargin
      }

    case ProcessingFailure(s, a, e1, e2, t) =>
      logger.error {
        s"""Failed Resultset Processing:
          |
          |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n")}
          |
          | arguments = [${a.mkString(", ")}]
          |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
          |   failure = ${t.getMessage}
         """.stripMargin
      }

    case ExecFailure(s, a, e1, t) =>
      logger.error {
        s"""Failed Statement Execution:
          |
          |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n")}
          |
          | arguments = [${a.mkString(", ")}]
          |   elapsed = ${e1.toMillis} ms exec (failed)
          |   failure = ${t.getMessage}
         """.stripMargin
      }
  }

}
