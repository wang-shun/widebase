package widebase.dsl.test

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

object DslCheck extends Logger with Loggable {

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  import widebase.db.table.Table

  val debug = false

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def main(args: Array[String]) {

    println(
      bool().typeOf +
      ", " +
      byte().typeOf +
      ", " +
      char().typeOf +
      ", " +
      double().typeOf +
      ", " +
      float().typeOf +
      ", " +
      int().typeOf +
      ", " +
      long().typeOf +
      ", " +
      short().typeOf +
      ", " +
      month().typeOf +
      ", " +
      date().typeOf +
      ", " +
      minute().typeOf +
      ", " +
      second().typeOf +
      ", " +
      time().typeOf +
      ", " +
      dateTime().typeOf +
      ", " +
      timestamp().typeOf +
      ", " +
      symbol().typeOf +
      ", " +
      string().typeOf)

    println("")

    println(R(
      127.b,
      32767.s,
      "2012-06".M,
      "2012-06-27".D,
      12.U,
      34.V,
      "22:31:59.123".T,
      "22:31:59.123456789".P,
      "2012-06-27T22:31:59.123".Z
    ))

    println("")

    val table = Table(
      string(
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
      bool(true, false),
      byte(Byte.MinValue, Byte.MaxValue),
      char(Char.MinValue, Char.MaxValue),
      double(Double.MinValue, Double.MaxValue),
      float(Float.MinValue, Float.MaxValue),
      int(Int.MinValue, Int.MaxValue),
      long(Long.MinValue, Long.MaxValue),
      short(Short.MinValue, Short.MaxValue),
      month(new YearMonth(millis), (new YearMonth(millis)).plusMonths(1)),
      date(new LocalDate(millis), (new LocalDate(millis)).plusDays(1)),
      minute(Minutes.minutes(12), Minutes.minutes(34)),
      second(Seconds.seconds(23), Seconds.seconds(45)),
      time(new LocalTime(millis), (new LocalTime(millis)).plusHours(1)),
      dateTime(new LocalDateTime(millis), (new LocalDateTime(millis)).plusYears(1)),
      timestamp(new Timestamp(millis), new Timestamp(millis + 86400000)),
      symbol('Hello, 'World),
      string("Hoi", "Wält!"))

    {

      val bools: BoolColumn = table("Bool")
      val bytes: ByteColumn = table("Byte")
      val chars: CharColumn = table("Char")
      val doubles: DoubleColumn = table("Double")
      val floats: FloatColumn = table("Float")
      val ints: IntColumn = table("Int")
      val longs: LongColumn = table("Long")
      val shorts: ShortColumn = table("Short")
      val months: MonthColumn = table("Month")
      val dates: DateColumn = table("Date")
      val minutes: MinuteColumn = table("Minute")
      val seconds: SecondColumn = table("Second")
      val times: TimeColumn = table("Time")
      val dateTimes: DateTimeColumn = table("DateTime")
      val timestamps: TimestampColumn = table("Timestamp")
      val symbols: SymbolColumn = table("Symbol")
      val strings: StringColumn = table("String")

      println(bools.typeOf + ": " + bools.head + " != " + bools.last)
      println(bytes.typeOf + ": " + bytes.head + " < " + bytes.last)
      println(chars.typeOf + ": " + chars.head.toInt + " < " + chars.last.toInt + " (as Int)")
      println(doubles.typeOf + ": " + doubles.head + " < " + doubles.last)
      println(floats.typeOf + ": " + floats.head + " < " + floats.last)
      println(ints.typeOf + ": " + ints.head + " < " + ints.last)
      println(longs.typeOf + ": " + longs.head + " < " + longs.last)
      println(shorts.typeOf + ": " + shorts.head + " < " + shorts.last)
      println(months.typeOf + ": " + months.head + " < " + months.last)
      println(dates.typeOf + ": " + dates.head + " < " + dates.last)
      println(minutes.typeOf + ": " + minutes.head + " < " + minutes.last)
      println(seconds.typeOf + ": " + seconds.head + " < " + seconds.last)
      println(times.typeOf + ": " + times.head + " < " + times.last)
      println(dateTimes.typeOf + ": " + dateTimes.head + " < " + dateTimes.last)
      println(timestamps.typeOf + ": " + timestamps.head + " < " + timestamps.last)
      println(symbols.typeOf + ": " + symbols.head + " " + symbols.last)
      println(strings.typeOf + ": " + strings.head + " " + strings.last)

    }

    println("")

    {

      val bools = table("Bool").b
      val bytes = table("Byte").x
      val chars = table("Char").c
      val doubles = table("Double").d
      val floats = table("Float").f
      val ints = table("Int").i
      val longs = table("Long").l
      val shorts = table("Short").s
      val months = table("Month").M
      val dates = table("Date").D
      val minutes = table("Minute").U
      val seconds = table("Second").V
      val times = table("Time").T
      val dateTimes = table("DateTime").Z
      val timestamps = table("Timestamp").P
      val symbols = table("Symbol").Y
      val strings = table("String").S

      println(bools.typeOf + ": " + bools.head + " != " + bools.last)
      println(bytes.typeOf + ": " + bytes.head + " < " + bytes.last)
      println(chars.typeOf + ": " + chars.head.toInt + " < " + chars.last.toInt + " (as Int)")
      println(doubles.typeOf + ": " + doubles.head + " < " + doubles.last)
      println(floats.typeOf + ": " + floats.head + " < " + floats.last)
      println(ints.typeOf + ": " + ints.head + " < " + ints.last)
      println(longs.typeOf + ": " + longs.head + " < " + longs.last)
      println(shorts.typeOf + ": " + shorts.head + " < " + shorts.last)
      println(months.typeOf + ": " + months.head + " < " + months.last)
      println(dates.typeOf + ": " + dates.head + " < " + dates.last)
      println(minutes.typeOf + ": " + minutes.head + " < " + minutes.last)
      println(seconds.typeOf + ": " + seconds.head + " < " + seconds.last)
      println(times.typeOf + ": " + times.head + " < " + times.last)
      println(dateTimes.typeOf + ": " + dateTimes.head + " < " + dateTimes.last)
      println(timestamps.typeOf + ": " + timestamps.head + " < " + timestamps.last)
      println(symbols.typeOf + ": " + symbols.head + " " + symbols.last)
      println(strings.typeOf + ": " + strings.head + " " + strings.last)

    }
 }
}

