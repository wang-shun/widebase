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

/** Test producer.
 *
 * @author myst3r10n
 */
object Producer extends Logger with Loggable {

  import widebase.stream.socket.rq

  protected var debug = false
  protected var records: Int = _
  protected var producer: widebase.stream.socket.rq.Producer = _

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 48

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

    producer = rq.producer

    try {

      producer.login("producer", "password")

      table("table", records)

    } finally {

      producer.close

    }
  }

  def table(name: String, records: Int) {

    var started = 0L

    started = System.currentTimeMillis
    for(r <- 0 to records - 1) {

      val table = Table(StringColumn(
        "Partition",
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
        DateTimeColumn((new LocalDateTime(millis)).plusHours(r)),
        BoolColumn(true),
        ByteColumn(Byte.MaxValue),
        CharColumn(Char.MaxValue),
        DoubleColumn(Double.MaxValue),
        FloatColumn(Float.MaxValue),
        IntColumn(Int.MaxValue),
        LongColumn(Long.MaxValue),
        ShortColumn(Short.MaxValue),
        MonthColumn(new YearMonth(millis)),
        DateColumn(new LocalDate(millis)),
        MinuteColumn(Minutes.minutes(12)),
        SecondColumn(Seconds.seconds(34)),
        TimeColumn(new LocalTime(millis)),
        DateTimeColumn(new LocalDateTime(millis)),
        TimestampColumn(new Timestamp(millis)),
        SymbolColumn('Hello),
        StringColumn("World!"))

      producer.publish("table", table)

    }
    info("Table published " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

