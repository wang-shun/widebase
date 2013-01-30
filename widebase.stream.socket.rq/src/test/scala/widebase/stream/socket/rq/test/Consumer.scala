package widebase.stream.socket.rq.test

import java.sql.Timestamp
import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import widebase.db.column. {

  BoolColumn,
  ByteColumn,
  CharColumn,
  DoubleColumn,
  FloatColumn,
  IntColumn,
  LongColumn,
  ShortColumn,
  MonthColumn,
  DateColumn,
  MinuteColumn,
  SecondColumn,
  TimeColumn,
  DateTimeColumn,
  TimestampColumn,
  SymbolColumn,
  StringColumn

}

import widebase.db.table.Table
import widebase.stream.handler.rq.RecordListener

/** Test consumer.
 *
 * @author myst3r10n
 */
object Consumer extends Logger with Loggable {

  import widebase.stream.socket.rq

  protected var debug = false
  protected var records: Int = _
  protected var table: String = _

  protected var consumer: widebase.stream.socket.rq.Consumer = _

  object listener extends RecordListener {

    var count = 0

    def react = {

      case text: String => println("event: " + text)
      case (records: Int, partition: String) => println("rollback: " + records + " @ " + partition)
      case chunk: Table =>
        println(chunk)

        count += 1

        if(count == records)
          consumer.close

    }
  }

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 10
    table = "table"

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-r" =>
          i += 1
          records = args(i).toInt

        case "-t" =>
          i += 1
          table = args(i)

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    consumer = rq.consumer(listener)

    try {

      consumer.login("consumer", "password").subscribe(table)

    } catch {

      case e => consumer.close

    }
  }
}

