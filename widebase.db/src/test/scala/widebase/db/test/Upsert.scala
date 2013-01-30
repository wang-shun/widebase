package widebase.db.test

import java.io.File
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

import scala.collection.mutable.ArrayBuffer

import widebase.data.Datatype

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

/* Test of upsert.
 *
 * @author myst3r10n
 */
object Upsert extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var records: Int = _

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 100

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-r" =>
          i += 1
          records = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    var started = 0L

    val table = Table(
      StringColumn(
        "Bool",
        "Byte",
        "Char",
        "Double",
        "Float",
        "Int",
        "Long",
        "Short",
        "Month",
        "Date",
        "Minute",
        "Second",
        "Time",
        "DateTime",
        "Timestamp",
        "Symbol",
        "String"),
      BoolColumn(),
      ByteColumn(),
      CharColumn(),
      DoubleColumn(),
      FloatColumn(),
      IntColumn(),
      LongColumn(),
      ShortColumn(),
      MonthColumn(),
      DateColumn(),
      MinuteColumn(),
      SecondColumn(),
      TimeColumn(),
      DateTimeColumn(),
      TimestampColumn(),
      SymbolColumn(),
      StringColumn())

    started = System.currentTimeMillis
    save.dir("upsert", table)
    info("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    var mapped = map("upsert")
    info("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    for(r <- 1 to records)
      upsert(
        "upsert",
        ArrayBuffer(
          true,
          Byte.MaxValue,
          Char.MaxValue,
          Double.MaxValue,
          Float.MaxValue,
          Int.MaxValue,
          Long.MaxValue,
          Short.MaxValue,
          new YearMonth(millis),
          new LocalDate(millis),
          Minutes.minutes(12),
          Seconds.seconds(34),
          new LocalTime(millis),
          new LocalDateTime(millis),
          new Timestamp(millis),
          'Hello,
          "World!"))
    info("Dir table upsert " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val remapped = map("upsert")
    info("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    var r = 0

    remapped.records.foreach { record =>

      r += 1

      if(debug || r == remapped.records.length)
        println("Record " + r + ": " + record)
      else
        record

    }
    info("Dir table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

