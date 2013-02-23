package widebase.testkit

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

import scala.collection.mutable.ArrayBuffer

import widebase.db.table.Table

/* Test of upsert.
 *
 * @author myst3r10n
 */
object Upsert extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  var debug: Boolean = _
  var records: Int = _

  def main(args: Array[String]) {

    records = 100

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
      datetime(),
      timestamp(),
      symbol(),
      string())

    started = System.currentTimeMillis
    save.dir("upsert", table)
    info("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    var mapped = map("upsert")
    info("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    for(r <- 1 to records)
      upsert(
        "upsert",
        ArrayBuffer(
          true,
          Byte.MaxValue,
          Char.MaxValue,
          Double.MaxValue,
          Float.MaxValue,
          Int.MaxValue,
          Long.MaxValue,
          Short.MaxValue,
          new YearMonth(millis),
          new LocalDate(millis),
          Minutes.minutes(12),
          Seconds.seconds(34),
          new LocalTime(millis),
          new LocalDateTime(millis),
          new Timestamp(millis),
          'Hello,
          "World!"))
    info("Dir table upsert " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    val remapped = map("upsert")
    info("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    remapped.records.foreach { record =>

      assert(record("Bool") == true, error("Value unexpected: " + record("Bool")))
      assert(record("Byte") == Byte.MaxValue, error("Value unexpected: " + record("Byte")))
      assert(record("Char") == Char.MaxValue, error("Value unexpected: " + record("Chart")))
      assert(record("Double") == Double.MaxValue, error("Value unexpected: " + record("Double")))
      assert(record("Float") == Float.MaxValue, error("Value unexpected: " + record("Float")))
      assert(record("Int") == Int.MaxValue, error("Value unexpected: " + record("Int")))
      assert(record("Long") == Long.MaxValue, error("Value unexpected: " + record("Long")))
      assert(record("Short") == Short.MaxValue, error("Value unexpected: " + record("Short")))
      assert(record("Month") == new YearMonth(millis), error("Value unexpected: " + record("Month")))
      assert(record("Date") == new LocalDate(millis), error("Value unexpected: " + record("Date")))
      assert(record("Minute") == Minutes.minutes(12), error("Value unexpected: " + record("Minute")))
      assert(record("Second") == Seconds.seconds(34), error("Value unexpected: " + record("Second")))
      assert(record("Time") == new LocalTime(millis), error("Value unexpected: " + record("Time")))
      assert(record("DateTime") == new LocalDateTime(millis), error("Value unexpected: " + record("DateTime")))
      assert(record("Timestamp") == new Timestamp(millis), error("Value unexpected: " + record("Timestamp")))
      assert(record("Symbol") == 'Hello, error("Value unexpected: " + record("Symbol")))
      assert(record("String") == "World!", error("Value unexpected: " + record("String")))

    }
    info("Dir table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

