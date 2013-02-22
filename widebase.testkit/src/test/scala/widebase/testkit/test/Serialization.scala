package widebase.testkit.test

import java.sql.Timestamp

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

import widebase.db.table.Table

/* Test of table serialization.
 *
 * @author myst3r10n
 */
object Serialization extends Logger with Loggable {

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  var records: Int = _

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  def main(args: Array[String]) {

    records = 25000

    var started = 0L

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
      bool(),
      byte(),
      char(),
      double(),
      float(),
      int(),
      long(),
      short(),
      month(),
      date(),
      minute(),
      second(),
      time(),
      dateTime(),
      timestamp(),
      symbol(),
      string())

    var boolCol = table("Bool").b
    var byteCol = table("Byte").x
    var charCol = table("Char").c
    var doubleCol = table("Double").d
    var floatCol = table("Float").f
    var intCol = table("Int").i
    var longCol = table("Long").l
    var shortCol = table("Short").s
    var monthCol = table("Month").M
    var dateCol = table("Date").D
    var minuteCol = table("Minute").U
    var secondCol = table("Second").V
    var timeCol = table("Time").T
    var dateTimeCol = table("DateTime").Z
    var timestampCol = table("Timestamp").P
    var symbolCol = table("Symbol").Y
    var stringCol = table("String").S

    started = System.currentTimeMillis
    for(r <- 1 to records) {

      boolCol += true
      byteCol += Byte.MaxValue
      charCol += Char.MaxValue
      doubleCol += Double.MaxValue
      floatCol += Float.MaxValue
      intCol += Int.MaxValue
      longCol += Long.MaxValue
      shortCol += Short.MaxValue
      monthCol += new YearMonth(millis)
      dateCol += new LocalDate(millis)
      minuteCol += Minutes.minutes(12)
      secondCol += Seconds.seconds(34)
      timeCol += new LocalTime(millis)
      dateTimeCol += new LocalDateTime(millis)
      timestampCol += new Timestamp(millis)
      symbolCol += 'Hello
      stringCol += "World!"

    }
    println("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val binary = table.toBytes()
    println("Table to bytes " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val recovered = Table.fromBytes(binary)
    println("Table from bytes " + records + " records in " +
      diff(started, System.currentTimeMillis))

    boolCol = recovered("Bool").b
    byteCol = recovered("Byte").x
    charCol = recovered("Char").c
    doubleCol = recovered("Double").d
    floatCol = recovered("Float").f
    intCol = recovered("Int").i
    longCol = recovered("Long").l
    shortCol = recovered("Short").s
    monthCol = recovered("Month").M
    dateCol = recovered("Date").D
    minuteCol = recovered("Minute").U
    secondCol = recovered("Second").V
    timeCol = recovered("Time").T
    dateTimeCol = recovered("DateTime").Z
    timestampCol = recovered("Timestamp").P
    symbolCol = recovered("Symbol").Y
    stringCol = recovered("String").S

    started = System.currentTimeMillis
    for(r <- 0 to records - 1) {

      assert(boolCol(r) == true, error("Value unexpected: " + boolCol(r)))
      assert(byteCol(r) == Byte.MaxValue, error("Value unexpected: " + byteCol(r)))
      assert(charCol(r) == Char.MaxValue, error("Value unexpected: " + charCol(r)))
      assert(doubleCol(r) == Double.MaxValue, error("Value unexpected: " + doubleCol(r)))
      assert(floatCol(r) == Float.MaxValue, error("Value unexpected: " + floatCol(r)))
      assert(intCol(r) == Int.MaxValue, error("Value unexpected: " + intCol(r)))
      assert(longCol(r) == Long.MaxValue, error("Value unexpected: " + longCol(r)))
      assert(shortCol(r) == Short.MaxValue, error("Value unexpected: " + shortCol(r)))
      assert(monthCol(r) == new YearMonth(millis), error("Value unexpected: " + monthCol(r)))
      assert(dateCol(r) == new LocalDate(millis), error("Value unexpected: " + dateCol(r)))
      assert(minuteCol(r) == Minutes.minutes(12), error("Value unexpected: " + minuteCol(r)))
      assert(secondCol(r) == Seconds.seconds(34), error("Value unexpected: " + secondCol(r)))
      assert(timeCol(r) == new LocalTime(millis), error("Value unexpected: " + timeCol(r)))
      assert(dateTimeCol(r) == new LocalDateTime(millis), error("Value unexpected: " + dateTimeCol(r)))
      assert(timestampCol(r) == new Timestamp(millis), error("Value unexpected: " + timestampCol(r)))
      assert(symbolCol(r) == 'Hello, error("Value unexpected: " + symbolCol(r)))
      assert(stringCol(r) == "World!", error("Value unexpected: " + stringCol(r)))

    }
    println("Table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

