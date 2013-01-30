package widebase.stream.socket.cq.test

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
import widebase.stream.socket.cq.Client

/** Test save into cached database and load from cached database.
 *
 * @author myst3r10n
 */
object Column extends Logger with Loggable {

  import widebase.stream.socket.cq

  protected var debug = false
  protected var filter: String = _
  protected var records: Int = _
  protected var client: Client = _

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 25000

    client = cq.client

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-f" =>
          i += 1
          client.filter(args(i))

        case "-r" =>
          i += 1
          records = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    try {

      client.login("client", "password")

      table("table", records)

    } catch {

      case e =>
        e.printStackTrace
        sys.exit(1)

    } finally {

      client.close

    }
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

    started = System.currentTimeMillis
    client.save(name, table)
    info("Table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = client.load(name)
    info("Table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

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

