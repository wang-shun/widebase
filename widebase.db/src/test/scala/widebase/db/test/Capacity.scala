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

/* Test of load, map and save operations with various buffer size.
 *
 * @author myst3r10n
 */
object Capacity extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var parts: Int = _
  protected var records: Int = _

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  val modes = Array(
    128,
    256,
    512,
    1024,
    2048,
    4096,
    8192,
    16384,
    32768,
    65536)

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    parts = 10
    records = 25000

    var i = 0

    while(i < args.length) {

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

    val table = Table(StringColumn(
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

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      save(name, table)
      info("Table saved " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

    table.columns.foreach(column => column.clear)

    var loaded: Table = null

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      loaded = load(name)
      info("Table loaded " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

    bools = loaded("Bool").asInstanceOf[BoolColumn]
    bytes = loaded("Byte").asInstanceOf[ByteColumn]
    chars = loaded("Char").asInstanceOf[CharColumn]
    doubles = loaded("Double").asInstanceOf[DoubleColumn]
    floats = loaded("Float").asInstanceOf[FloatColumn]
    ints = loaded("Int").asInstanceOf[IntColumn]
    longs = loaded("Long").asInstanceOf[LongColumn]
    shorts = loaded("Short").asInstanceOf[ShortColumn]
    months = loaded("Month").asInstanceOf[MonthColumn]
    dates = loaded("Date").asInstanceOf[DateColumn]
    minutes = loaded("Minute").asInstanceOf[MinuteColumn]
    seconds = loaded("Second").asInstanceOf[SecondColumn]
    times = loaded("Time").asInstanceOf[TimeColumn]
    dateTimes = loaded("DateTime").asInstanceOf[DateTimeColumn]
    timestamps = loaded("Timestamp").asInstanceOf[TimestampColumn]
    symbols = loaded("Symbol").asInstanceOf[SymbolColumn]
    strings = loaded("String").asInstanceOf[StringColumn]

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      for(r <- 0 to records - 1)
        if(debug) {

          println(
            bools(r),
            bytes(r),
            chars(r).toInt,
            doubles(r),
            floats(r),
            ints(r),
            longs(r),
            shorts(r),
            months(r),
            dates(r),
            minutes(r),
            seconds(r),
            times(r),
            dateTimes(r),
            timestamps(r),
            symbols(r),
            strings(r))

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
        diff(started, System.currentTimeMillis) + " by + " + mode + " bytes")

      if(mode == modes.last)
        println(
          bools.last,
          bytes.last,
          chars.last.toInt,
          doubles.last,
          floats.last,
          ints.last,
          longs.last,
          shorts.last,
          months.last,
          dates.last,
          minutes.last,
          seconds.last,
          times.last,
          dateTimes.last,
          timestamps.last,
          symbols.last,
          strings.last)

    }
  }

  def dirTable(name: String, records: Int) {

    var started = 0L

    val table = Table(StringColumn(
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
    info("Dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      save.dir(name, table)
      info("Dir table saved " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }


    table.columns.foreach(column => column.clear)

    var loaded: Table = null

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      loaded = load.dir(name)
      info("Dir table loaded " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

    bools = loaded("Bool").asInstanceOf[BoolColumn]
    bytes = loaded("Byte").asInstanceOf[ByteColumn]
    chars = loaded("Char").asInstanceOf[CharColumn]
    doubles = loaded("Double").asInstanceOf[DoubleColumn]
    floats = loaded("Float").asInstanceOf[FloatColumn]
    ints = loaded("Int").asInstanceOf[IntColumn]
    longs = loaded("Long").asInstanceOf[LongColumn]
    shorts = loaded("Short").asInstanceOf[ShortColumn]
    months = loaded("Month").asInstanceOf[MonthColumn]
    dates = loaded("Date").asInstanceOf[DateColumn]
    minutes = loaded("Minute").asInstanceOf[MinuteColumn]
    seconds = loaded("Second").asInstanceOf[SecondColumn]
    times = loaded("Time").asInstanceOf[TimeColumn]
    dateTimes = loaded("DateTime").asInstanceOf[DateTimeColumn]
    timestamps = loaded("Timestamp").asInstanceOf[TimestampColumn]
    symbols = loaded("Symbol").asInstanceOf[SymbolColumn]
    strings = loaded("String").asInstanceOf[StringColumn]

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      for(r <- 0 to records - 1)
        if(debug) {

          println(
            bools(r),
            bytes(r),
            chars(r).toInt,
            doubles(r),
            floats(r),
            ints(r),
            longs(r),
            shorts(r),
            months(r),
            dates(r),
            minutes(r),
            seconds(r),
            times(r),
            dateTimes(r),
            timestamps(r),
            symbols(r),
            strings(r))

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

      info("Dir table loaded iterated " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by + " + mode + " bytes")

      if(mode == modes.last)
        println(
          bools.last,
          bytes.last,
          chars.last.toInt,
          doubles.last,
          floats.last,
          ints.last,
          longs.last,
          shorts.last,
          months.last,
          dates.last,
          minutes.last,
          seconds.last,
          times.last,
          dateTimes.last,
          timestamps.last,
          symbols.last,
          strings.last)

    }

    loaded.columns.foreach(column => column.clear)

    var mapped: Table = null

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      mapped = map(name)
      info("Dir table mapped " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

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

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      for(r <- 0 to records - 1)
        if(debug) {

          println(
            bools(r),
            bytes(r),
            chars(r).toInt,
            doubles(r),
            floats(r),
            ints(r),
            longs(r),
            shorts(r),
            months(r),
            dates(r),
            minutes(r),
            seconds(r),
            times(r),
            dateTimes(r),
            timestamps(r),
            symbols(r),
            strings(r))

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

      info("Dir table mapped iterated " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by + " + mode + " bytes")

      if(mode == modes.last)
        println(
          bools.last,
          bytes.last,
          chars.last.toInt,
          doubles.last,
          floats.last,
          ints.last,
          longs.last,
          shorts.last,
          months.last,
          dates.last,
          minutes.last,
          seconds.last,
          times.last,
          dateTimes.last,
          timestamps.last,
          symbols.last,
          strings.last)

    }
  }

  def partedDirTable(
    name: String,
    parts: Int,
    records: Int) {

    var started = 0L

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
      new DateColumn,
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

    var partitions = table("Partition").asInstanceOf[DateColumn]
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

    var loaded: Iterable[Table] = null

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      loaded = load.dates(
        name,
        new LocalDate(millis),
        new LocalDate(millis).plusDays(parts)).tables
      info("Parted dir table loaded " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      var loadedP = 0

      started = System.currentTimeMillis
      for(table <- loaded) {

        loadedP += 1

        partitions = table("Partition").asInstanceOf[DateColumn]
        bools = table("Bool").asInstanceOf[BoolColumn]
        bytes = table("Byte").asInstanceOf[ByteColumn]
        chars = table("Char").asInstanceOf[CharColumn]
        doubles = table("Double").asInstanceOf[DoubleColumn]
        floats = table("Float").asInstanceOf[FloatColumn]
        ints = table("Int").asInstanceOf[IntColumn]
        longs = table("Long").asInstanceOf[LongColumn]
        shorts = table("Short").asInstanceOf[ShortColumn]
        months = table("Month").asInstanceOf[MonthColumn]
        dates = table("Date").asInstanceOf[DateColumn]
        minutes = table("Minute").asInstanceOf[MinuteColumn]
        seconds = table("Second").asInstanceOf[SecondColumn]
        times = table("Time").asInstanceOf[TimeColumn]
        dateTimes = table("DateTime").asInstanceOf[DateTimeColumn]
        timestamps = table("Timestamp").asInstanceOf[TimestampColumn]
        symbols = table("Symbol").asInstanceOf[SymbolColumn]
        strings = table("String").asInstanceOf[StringColumn]

        for(r <- 0 to records / parts - 1)
          if(debug) {

            println(
              partitions(r),
              bools(r),
              bytes(r),
              chars(r).toInt,
              doubles(r),
              floats(r),
              ints(r),
              longs(r),
              shorts(r),
              months(r),
              dates(r),
              minutes(r),
              seconds(r),
              times(r),
              dateTimes(r),
              timestamps(r),
              symbols(r),
              strings(r))

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

      info("Parted dir table loaded iterated " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by + " + mode + " bytes")

      if(mode == modes.last)
        println(
          loaded.last("Partition").asInstanceOf[DateColumn].last,
          loaded.last("Bool").asInstanceOf[BoolColumn].last,
          loaded.last("Byte").asInstanceOf[ByteColumn].last,
          loaded.last("Char").asInstanceOf[CharColumn].last.toInt,
          loaded.last("Double").asInstanceOf[DoubleColumn].last,
          loaded.last("Float").asInstanceOf[FloatColumn].last,
          loaded.last("Int").asInstanceOf[IntColumn].last,
          loaded.last("Long").asInstanceOf[LongColumn].last,
          loaded.last("Short").asInstanceOf[ShortColumn].last,
          loaded.last("Month").asInstanceOf[MonthColumn].last,
          loaded.last("Date").asInstanceOf[DateColumn].last,
          loaded.last("Minute").asInstanceOf[MinuteColumn].last,
          loaded.last("Second").asInstanceOf[SecondColumn].last,
          loaded.last("Time").asInstanceOf[TimeColumn].last,
          loaded.last("DateTime").asInstanceOf[DateTimeColumn].last,
          loaded.last("Timestamp").asInstanceOf[TimestampColumn].last,
          loaded.last("Symbol").asInstanceOf[SymbolColumn].last,
          loaded.last("String").asInstanceOf[StringColumn].last)

    }

    loaded.foreach(table => table.columns.foreach(column => column.clear))

    var mapped: Iterable[Table] = null

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      started = System.currentTimeMillis
      mapped = map.dates(
        name,
        new LocalDate(millis),
        new LocalDate(millis).plusDays(parts)).tables
      info("Parted dir table mapped " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by " + mode + " bytes")

    }

    modes.foreach { mode =>

      System.setProperty("widebase.io.capacity", mode.toString)

      var mappedP = 0

      started = System.currentTimeMillis
      for(table <- mapped) {

        mappedP += 1

        partitions = table("Partition").asInstanceOf[DateColumn]
        bools = table("Bool").asInstanceOf[BoolColumn]
        bytes = table("Byte").asInstanceOf[ByteColumn]
        chars = table("Char").asInstanceOf[CharColumn]
        doubles = table("Double").asInstanceOf[DoubleColumn]
        floats = table("Float").asInstanceOf[FloatColumn]
        ints = table("Int").asInstanceOf[IntColumn]
        longs = table("Long").asInstanceOf[LongColumn]
        shorts = table("Short").asInstanceOf[ShortColumn]
        months = table("Month").asInstanceOf[MonthColumn]
        dates = table("Date").asInstanceOf[DateColumn]
        minutes = table("Minute").asInstanceOf[MinuteColumn]
        seconds = table("Second").asInstanceOf[SecondColumn]
        times = table("Time").asInstanceOf[TimeColumn]
        dateTimes = table("DateTime").asInstanceOf[DateTimeColumn]
        timestamps = table("Timestamp").asInstanceOf[TimestampColumn]
        symbols = table("Symbol").asInstanceOf[SymbolColumn]
        strings = table("String").asInstanceOf[StringColumn]

        for(r <- 0 to records / parts - 1)
          if(debug) {

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

      info("Parted dir table mapped iterated " + records + " records in " +
        diff(started, System.currentTimeMillis) + " by + " + mode + " bytes")

      if(mode == modes.last)
        println(
          mapped.last("Partition").asInstanceOf[DateColumn].last,
          mapped.last("Bool").asInstanceOf[BoolColumn].last,
          mapped.last("Byte").asInstanceOf[ByteColumn].last,
          mapped.last("Char").asInstanceOf[CharColumn].last.toInt,
          mapped.last("Double").asInstanceOf[DoubleColumn].last,
          mapped.last("Float").asInstanceOf[FloatColumn].last,
          mapped.last("Int").asInstanceOf[IntColumn].last,
          mapped.last("Long").asInstanceOf[LongColumn].last,
          mapped.last("Short").asInstanceOf[ShortColumn].last,
          mapped.last("Month").asInstanceOf[MonthColumn].last,
          mapped.last("Date").asInstanceOf[DateColumn].last,
          mapped.last("Minute").asInstanceOf[MinuteColumn].last,
          mapped.last("Second").asInstanceOf[SecondColumn].last,
          mapped.last("Time").asInstanceOf[TimeColumn].last,
          mapped.last("DateTime").asInstanceOf[DateTimeColumn].last,
          mapped.last("Timestamp").asInstanceOf[TimestampColumn].last,
          mapped.last("Symbol").asInstanceOf[SymbolColumn].last,
          mapped.last("String").asInstanceOf[StringColumn].last)

    }
  }
}

