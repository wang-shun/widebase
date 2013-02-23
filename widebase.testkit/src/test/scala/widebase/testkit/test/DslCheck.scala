package widebase.testkit.test

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

import org.joda.time.format.DateTimeFormat

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

object DslCheck extends Logger with Loggable {

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  val debug = false

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

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

      assert(bools.head != bools.last, error("Value unexpected: " + bools.last))
      assert(bytes.head < bytes.last, error("Value unexpected: " + bytes.last))
      assert(chars.head.toInt < chars.last.toInt, error("Value unexpected: " + chars.last.toInt))
      assert(doubles.head < doubles.last, error("Value unexpected: " + doubles.last))
      assert(floats.head < floats.last, error("Value unexpected: " + floats.last))
      assert(ints.head < ints.last, error("Value unexpected: " + ints.last))
      assert(longs.head < longs.last, error("Value unexpected: " + longs.last))
      assert(shorts.head < shorts.last, error("Value unexpected: " + shorts.last))
      assert(months.head.compareTo(months.last) == -1, error("Value unexpected: " + months.last))
      assert(dates.head.compareTo(dates.last) == -1, error("Value unexpected: " + dates.last))
      assert(minutes.head.compareTo(minutes.last) == -1, error("Value unexpected: " + minutes.last))
      assert(seconds.head.compareTo(seconds.last) == -1, error("Value unexpected: " + seconds.last))
      assert(times.head.compareTo(times.last) == -1, error("Value unexpected: " + times.last))
      assert(dateTimes.head.compareTo(dateTimes.last) == -1, error("Value unexpected: " + dateTimes.last))
      assert(timestamps.head.compareTo(timestamps.last) == -1, error("Value unexpected: " + timestamps.last))
      assert(symbols.head == 'Hello, error("Value unexpected: " + symbols.last))
      assert(symbols.last == 'World, error("Value unexpected: " + symbols.last))
      assert(strings.head == "Hoi", error("Value unexpected: " + strings.last))
      assert(strings.last == "Wält!", error("Value unexpected: " + strings.last))

    }

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

      assert(bools.head != bools.last, error("Value unexpected: " + bools.last))
      assert(bytes.head < bytes.last, error("Value unexpected: " + bytes.last))
      assert(chars.head.toInt < chars.last.toInt, error("Value unexpected: " + chars.last.toInt))
      assert(doubles.head < doubles.last, error("Value unexpected: " + doubles.last))
      assert(floats.head < floats.last, error("Value unexpected: " + floats.last))
      assert(ints.head < ints.last, error("Value unexpected: " + ints.last))
      assert(longs.head < longs.last, error("Value unexpected: " + longs.last))
      assert(shorts.head < shorts.last, error("Value unexpected: " + shorts.last))
      assert(months.head.compareTo(months.last) == -1, error("Value unexpected: " + months.last))
      assert(dates.head.compareTo(dates.last) == -1, error("Value unexpected: " + dates.last))
      assert(minutes.head.compareTo(minutes.last) == -1, error("Value unexpected: " + minutes.last))
      assert(seconds.head.compareTo(seconds.last) == -1, error("Value unexpected: " + seconds.last))
      assert(times.head.compareTo(times.last) == -1, error("Value unexpected: " + times.last))
      assert(dateTimes.head.compareTo(dateTimes.last) == -1, error("Value unexpected: " + dateTimes.last))
      assert(timestamps.head.compareTo(timestamps.last) == -1, error("Value unexpected: " + timestamps.last))
      assert(symbols.head == 'Hello, error("Value unexpected: " + symbols.last))
      assert(symbols.last == 'World, error("Value unexpected: " + symbols.last))
      assert(strings.head == "Hoi", error("Value unexpected: " + strings.last))
      assert(strings.last == "Wält!", error("Value unexpected: " + strings.last))

    }
 }
}
