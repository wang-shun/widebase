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

import scala.collection.mutable.LinkedHashSet

import vario.data.Datatype

import widebase.db.column.VariantColumn
import widebase.db.table.Table

/* Test of load, map and save operations with native columns.
 *
 * @author myst3r10n
 */
object Column extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var parts: Int = _
  protected var records: Int = _

  // Init DB
  var dir = new File("usr")

  if(!dir.exists)
    dir.mkdir

  dir = new File(dir.getPath + "/wdb")

  if(!dir.exists)
    dir.mkdir

  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/" + dir.getPath)

  // Init API
  import dbi.tables._

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    parts = 10
    records = 25000

    var i = 0

    while(i < args.size) {

      args(i) match {

        case "-d" => debug = true

        case "-p" =>
          i += 1
          parts = args(i).toInt

        case "-r" =>
          i += 1
          records = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    if(records / parts <= 0) {

      error("records / parts must be >= 1")
      sys.exit(1)

    }

    table("table", records)
    println("")
    dirTable("dirTable", records)
    println("")
    partedDirTable("dirTable", parts, records)

  }

  def table(name: String, records: Int) {

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
    save(name, table)
    info("Table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load(name)
    info("Table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    bools = loaded("Bool").bools
    bytes = loaded("Byte").bytes
    chars = loaded("Char").chars
    doubles = loaded("Double").doubles
    floats = loaded("Float").floats
    ints = loaded("Int").ints
    longs = loaded("Long").longs
    shorts = loaded("Short").shorts
    months = loaded("Month").months
    dates = loaded("Date").dates
    minutes = loaded("Minute").minutes
    seconds = loaded("Second").seconds
    times = loaded("Time").times
    dateTimes = loaded("DateTime").dateTimes
    timestamps = loaded("Timestamp").timestamps
    symbols = loaded("Symbol").symbols
    strings = loaded("String").strings

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

  def dirTable(name: String, records: Int) {

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
    info("Dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)
    info("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val mapped = map(name)
    info("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    bools = mapped("Bool").bools
    bytes = mapped("Byte").bytes
    chars = mapped("Char").chars
    doubles = mapped("Double").doubles
    floats = mapped("Float").floats
    ints = mapped("Int").ints
    longs = mapped("Long").longs
    shorts = mapped("Short").shorts
    months = mapped("Month").months
    dates = mapped("Date").dates
    minutes = mapped("Minute").minutes
    seconds = mapped("Second").seconds
    times = mapped("Time").times
    dateTimes = mapped("DateTime").dateTimes
    timestamps = mapped("Timestamp").timestamps
    symbols = mapped("Symbol").symbols
    strings = mapped("String").strings

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

    info("Dir table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def partedDirTable(
    name: String,
    parts: Int,
    records: Int) {

    var started = 0L

    val table = new Table(LinkedHashSet(
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
      new VariantColumn(Datatype.Date),
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

    var partitions = table("Partition").dates
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

    var partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(p <- 1 to parts) {

      for(r <- 1 to records / parts) {

        partitions += partition
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

      partition = partition.plusDays(1)

    }
    info("Parted dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)('daily)
    info("Parted dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val tables = map.dates(
      name,
      new LocalDate(millis),
      new LocalDate(millis).plusDays(parts)).tables
    info("Parted dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    var p = 0

    started = System.currentTimeMillis
    for(table <- tables) {

      p += 1

      partitions = table("Partition").dates
      bools = table("Bool").bools
      bytes = table("Byte").bytes
      chars = table("Char").chars
      doubles = table("Double").doubles
      floats = table("Float").floats
      ints = table("Int").ints
      longs = table("Long").longs
      shorts = table("Short").shorts
      months = table("Month").months
      dates = table("Date").dates
      minutes = table("Minute").minutes
      seconds = table("Second").seconds
      times = table("Time").times
      dateTimes = table("DateTime").dateTimes
      timestamps = table("Timestamp").timestamps
      symbols = table("Symbol").symbols
      strings = table("String").strings

      for(r <- 0 to records / parts - 1)
        if(debug || (p == parts && r == records / parts - 1)) {

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
    }

    info("Parted dir table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

