package widebase.stream.socket.rq.test

import java.sql.Timestamp
import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. {

  DateTimeZone,
  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import scala.collection.mutable. { ArrayBuffer, Map }

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

import widebase.stream.handler.rq. {

  ConsumerWriter,
  PersistenceHandler,
  PersistenceWriter

}

/* Test of upsert function.
 *
 * @author myst3r10n
 */
object Upsert extends Logger with Loggable {

  import widebase.db

  protected var debug: Boolean = _
  protected var parts: Int = _
  protected var records: Int = _

  // Init DB
  val dbi = db.instance(System.getProperty("user.dir") + "/usr/sync/test/db")

  // Init API
  import dbi.tables._

  // Init persistence
  object persistence extends PersistenceHandler {

    protected val persistences = Map[String, PersistenceWriter]()
    protected val subscriptions = Map[String, ArrayBuffer[ConsumerWriter]]()

    val path = dbi.path

  }

  val millisA = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S z")
    .parse("2012-01-23 12:34:56.789 GMT+00:00").getTime

  val millisB = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S z")
    .parse("2012-01-24 12:34:56.789 GMT+00:00").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 1000

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

    partedDirTable("columndDir",  records)

  }

  def partedDirTable(name: String, records: Int) {

    var started = 0L

    val tableA = Table(StringColumn(
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
      "String"))

    val tableB = Table(StringColumn(
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
      "String"))

    tableA += (
      new LocalDate(millisA),
      true,
      Byte.MaxValue,
      Char.MaxValue,
      Double.MaxValue,
      Float.MaxValue,
      Int.MaxValue,
      Long.MaxValue,
      Short.MaxValue,
      new YearMonth(millisA),
      new LocalDate(millisA),
      Minutes.minutes(12),
      Seconds.seconds(34),
      new LocalTime(millisA),
      new LocalDateTime(millisA),
      new Timestamp(millisA),
      'Hello,
      "World!")

    tableB += (
      new LocalDate(millisB, DateTimeZone.UTC),
      true,
      Byte.MaxValue,
      Char.MaxValue,
      Double.MaxValue,
      Float.MaxValue,
      Int.MaxValue,
      Long.MaxValue,
      Short.MaxValue,
      new YearMonth(millisB),
      new LocalDate(millisB),
      Minutes.minutes(12),
      Seconds.seconds(34),
      new LocalTime(millisB),
      new LocalDateTime(millisB),
      new Timestamp(millisB),
      'Hello,
      "World!")

    started = System.currentTimeMillis
    for(r <- 1 to records)
      persistence.upsert(name, tableA)
    info("Table A upserted " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    persistence.flush(name)
    info("Table A flushed " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    for(r <- 1 to records)
      persistence.upsert(name, tableB)
    info("Table B upserted " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    persistence.flush(name)
    info("Table B flushed " + records + " records in " +
      diff(started, System.currentTimeMillis))

    tableA.columns.foreach(column => column.clear)
    tableB.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    var mapped = map(name)("2012-01-23")
    info("Table A mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    var partitions = mapped("Partition").asInstanceOf[DateColumn]
    var bools = mapped("Bool").asInstanceOf[BoolColumn]
    var bytes = mapped("Byte").asInstanceOf[ByteColumn]
    var chars = mapped("Char").asInstanceOf[CharColumn]
    var doubles = mapped("Double").asInstanceOf[DoubleColumn]
    var floats = mapped("Float").asInstanceOf[FloatColumn]
    var ints = mapped("Int").asInstanceOf[IntColumn]
    var longs = mapped("Long").asInstanceOf[LongColumn]
    var shorts = mapped("Short").asInstanceOf[ShortColumn]
    var months = mapped("Month").asInstanceOf[MonthColumn]
    var dates = mapped("Date").asInstanceOf[DateColumn]
    var minutes = mapped("Minute").asInstanceOf[MinuteColumn]
    var seconds = mapped("Second").asInstanceOf[SecondColumn]
    var times = mapped("Time").asInstanceOf[TimeColumn]
    var dateTimes = mapped("DateTime").asInstanceOf[DateTimeColumn]
    var timestamps = mapped("Timestamp").asInstanceOf[TimestampColumn]
    var symbols = mapped("Symbol").asInstanceOf[SymbolColumn]
    var strings = mapped("String").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 0 to records - 1)
      if(debug || r == records - 1) {

        println("Partition: " + partitions(r))
        println("Bool: " + bools(r))
        println("Byte: " + bytes(r))
        println("Char: " + chars(r).toInt + " (as Int)")
        println("Double: " + doubles(r))
        println("Float: " + floats(r))
        println("Int: " + ints(r))
        println("Long: " + longs(r))
        println("Short: " + shorts(r))
        println("Month: " + months(r))
        println("Date: " + dates(r))
        println("Minute: " + minutes(r))
        println("Second: " + seconds(r))
        println("Time: " + times(r))
        println("DateTime: " + dateTimes(r))
        println("Timestamp: " + timestamps(r))
        println("Symbol: " + symbols(r))
        println("String: " + strings(r))

      } else {

        partitions(r)
        bools(r)
        bytes(r)
        chars(r)
        doubles(r)
        floats(r)
        ints(r)
        longs(r)
        shorts(r)
        months(r)
        dates(r)
        minutes(r)
        seconds(r)
        times(r)
        dateTimes(r)
        timestamps(r)
        symbols(r)
        strings(r)

      }

    info("Table A iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    mapped = map(name)("2012-01-24")
    info("Table B mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    partitions = mapped("Partition").asInstanceOf[DateColumn]
    bools = mapped("Bool").asInstanceOf[BoolColumn]
    bytes = mapped("Byte").asInstanceOf[ByteColumn]
    chars = mapped("Char").asInstanceOf[CharColumn]
    doubles = mapped("Double").asInstanceOf[DoubleColumn]
    floats = mapped("Float").asInstanceOf[FloatColumn]
    ints = mapped("Int").asInstanceOf[IntColumn]
    longs = mapped("Long").asInstanceOf[LongColumn]
    shorts = mapped("Short").asInstanceOf[ShortColumn]
    months = mapped("Month").asInstanceOf[MonthColumn]
    dates = mapped("Date").asInstanceOf[DateColumn]
    minutes = mapped("Minute").asInstanceOf[MinuteColumn]
    seconds = mapped("Second").asInstanceOf[SecondColumn]
    times = mapped("Time").asInstanceOf[TimeColumn]
    dateTimes = mapped("DateTime").asInstanceOf[DateTimeColumn]
    timestamps = mapped("Timestamp").asInstanceOf[TimestampColumn]
    symbols = mapped("Symbol").asInstanceOf[SymbolColumn]
    strings = mapped("String").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 0 to records - 1)
      if(debug || r == records - 1) {

        println("Partition: " + partitions(r))
        println("Bool: " + bools(r))
        println("Byte: " + bytes(r))
        println("Char: " + chars(r).toInt + " (as Int)")
        println("Double: " + doubles(r))
        println("Float: " + floats(r))
        println("Int: " + ints(r))
        println("Long: " + longs(r))
        println("Short: " + shorts(r))
        println("Month: " + months(r))
        println("Date: " + dates(r))
        println("Minute: " + minutes(r))
        println("Second: " + seconds(r))
        println("Time: " + times(r))
        println("DateTime: " + dateTimes(r))
        println("Timestamp: " + timestamps(r))
        println("Symbol: " + symbols(r))
        println("String: " + strings(r))

      } else {

        partitions(r)
        bools(r)
        bytes(r)
        chars(r)
        doubles(r)
        floats(r)
        ints(r)
        longs(r)
        shorts(r)
        months(r)
        dates(r)
        minutes(r)
        seconds(r)
        times(r)
        dateTimes(r)
        timestamps(r)
        symbols(r)
        strings(r)

      }

    info("Table B iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

