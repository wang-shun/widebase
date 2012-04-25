package widebase.db.test

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

import vario.data.Datatype

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

/* Test of table serialization.
 *
 * @author myst3r10n
 */
object Serialization extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var records: Int = _

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 25000

    var i = 0

    while(i < args.size) {

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

    val table = new Table(StringColumn(
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
      new BoolColumn,
      new ByteColumn,
      new CharColumn,
      new DoubleColumn,
      new FloatColumn,
      new IntColumn,
      new LongColumn,
      new ShortColumn,
      new MonthColumn,
      new DateColumn,
      new MinuteColumn,
      new SecondColumn,
      new TimeColumn,
      new DateTimeColumn,
      new TimestampColumn,
      new SymbolColumn,
      new StringColumn)

    var bools = table("Bool").asInstanceOf[BoolColumn]
    var bytes = table("Byte").asInstanceOf[ByteColumn]
    var chars = table("Char").asInstanceOf[CharColumn]
    var doubles = table("Double").asInstanceOf[DoubleColumn]
    var floats = table("Float").asInstanceOf[FloatColumn]
    var ints = table("Int").asInstanceOf[IntColumn]
    var longs = table("Long").asInstanceOf[LongColumn]
    var shorts = table("Short").asInstanceOf[ShortColumn]
    var months = table("Month").asInstanceOf[MonthColumn]
    var dates = table("Date").asInstanceOf[DateColumn]
    var minutes = table("Minute").asInstanceOf[MinuteColumn]
    var seconds = table("Second").asInstanceOf[SecondColumn]
    var times = table("Time").asInstanceOf[TimeColumn]
    var dateTimes = table("DateTime").asInstanceOf[DateTimeColumn]
    var timestamps = table("Timestamp").asInstanceOf[TimestampColumn]
    var symbols = table("Symbol").asInstanceOf[SymbolColumn]
    var strings = table("String").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 1 to records) {

      bools += true
      bytes += Byte.MaxValue
      chars += Char.MaxValue
      doubles += Double.MaxValue
      floats += Float.MaxValue
      ints += Int.MaxValue
      longs += Long.MaxValue
      shorts += Short.MaxValue
      months += new YearMonth(millis)
      dates += new LocalDate(millis)
      minutes += Minutes.minutes(12)
      seconds += Seconds.seconds(34)
      times += new LocalTime(millis)
      dateTimes += new LocalDateTime(millis)
      timestamps += new Timestamp(millis)
      symbols += 'Hello
      strings += "World!"

    }
    info("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val binary = table.toBytes()
    info("Table to bytes " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val recovered = Table.fromBytes(binary)
    info("Table from bytes " + records + " records in " +
      diff(started, System.currentTimeMillis))

    bools = recovered("Bool").asInstanceOf[BoolColumn]
    bytes = recovered("Byte").asInstanceOf[ByteColumn]
    chars = recovered("Char").asInstanceOf[CharColumn]
    doubles = recovered("Double").asInstanceOf[DoubleColumn]
    floats = recovered("Float").asInstanceOf[FloatColumn]
    ints = recovered("Int").asInstanceOf[IntColumn]
    longs = recovered("Long").asInstanceOf[LongColumn]
    shorts = recovered("Short").asInstanceOf[ShortColumn]
    months = recovered("Month").asInstanceOf[MonthColumn]
    dates = recovered("Date").asInstanceOf[DateColumn]
    minutes = recovered("Minute").asInstanceOf[MinuteColumn]
    seconds = recovered("Second").asInstanceOf[SecondColumn]
    times = recovered("Time").asInstanceOf[TimeColumn]
    dateTimes = recovered("DateTime").asInstanceOf[DateTimeColumn]
    timestamps = recovered("Timestamp").asInstanceOf[TimestampColumn]
    symbols = recovered("Symbol").asInstanceOf[SymbolColumn]
    strings = recovered("String").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 0 to records - 1)
      if(debug || r == records - 1) {

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

    info("Table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

