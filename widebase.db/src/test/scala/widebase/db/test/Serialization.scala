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

import scala.collection.mutable.LinkedHashSet

import vario.data.Datatype

import widebase.db.column.VariantColumn
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

    val table = new Table(LinkedHashSet(
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
      new VariantColumn(Datatype.Bool),
      new VariantColumn(Datatype.Byte),
      new VariantColumn(Datatype.Char),
      new VariantColumn(Datatype.Double),
      new VariantColumn(Datatype.Float),
      new VariantColumn(Datatype.Int),
      new VariantColumn(Datatype.Long),
      new VariantColumn(Datatype.Short),
      new VariantColumn(Datatype.Month),
      new VariantColumn(Datatype.Date),
      new VariantColumn(Datatype.Minute),
      new VariantColumn(Datatype.Second),
      new VariantColumn(Datatype.Time),
      new VariantColumn(Datatype.DateTime),
      new VariantColumn(Datatype.Timestamp),
      new VariantColumn(Datatype.Symbol),
      new VariantColumn(Datatype.String))

    var bools = table("Bool").bools
    var bytes = table("Byte").bytes
    var chars = table("Char").chars
    var doubles = table("Double").doubles
    var floats = table("Float").floats
    var ints = table("Int").ints
    var longs = table("Long").longs
    var shorts = table("Short").shorts
    var months = table("Month").months
    var dates = table("Date").dates
    var minutes = table("Minute").minutes
    var seconds = table("Second").seconds
    var times = table("Time").times
    var dateTimes = table("DateTime").dateTimes
    var timestamps = table("Timestamp").timestamps
    var symbols = table("Symbol").symbols
    var strings = table("String").strings

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

    bools = recovered("Bool").bools
    bytes = recovered("Byte").bytes
    chars = recovered("Char").chars
    doubles = recovered("Double").doubles
    floats = recovered("Float").floats
    ints = recovered("Int").ints
    longs = recovered("Long").longs
    shorts = recovered("Short").shorts
    months = recovered("Month").months
    dates = recovered("Date").dates
    minutes = recovered("Minute").minutes
    seconds = recovered("Second").seconds
    times = recovered("Time").times
    dateTimes = recovered("DateTime").dateTimes
    timestamps = recovered("Timestamp").timestamps
    symbols = recovered("Symbol").symbols
    strings = recovered("String").strings

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

