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

import widebase.db.table.Table

/* Test of load, map and save operations with records.
 *
 * @author myst3r10n
 */
object Record extends Logger with Loggable {

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

  var parts: Int = _
  var records: Int = _

  def main(args: Array[String]) {

    parts = 10
    records = 2500

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
        "String"))

    started = System.currentTimeMillis
    for(r <- 1 to records)
      table += (
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
        "World!")
    println("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save(name, table)
    println("Table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load(name)
    println("Table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    loaded.records.foreach { record =>

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
    println("Table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def dirTable(name: String, records: Int) {

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
        "String"))

    started = System.currentTimeMillis
    for(r <- 1 to records)
      table += (
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
        "World!")
    println("Dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)
    println("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load.dir(name)
    println("Dir table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    loaded.records.foreach { record =>

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
    println("Dir table loaded iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

    loaded.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val mapped = map(name)
    println("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    mapped.records.foreach { record =>

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
    println("Dir table mapped iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }

  def partedDirTable(
    name: String,
    parts: Int,
    records: Int) {

    var started = 0L

    val table = Table(
      string(
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

    var partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(p <- 1 to parts) {

      for(r <- 1 to records / parts)
        table += (
          partition,
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
          "World!")

      partition = partition.plusDays(1)

    }
    println("Parted dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)('daily)
    println("Parted dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load.dates(
      name,
      new LocalDate(millis),
      new LocalDate(millis).plusDays(parts)).tables
    println("Parted dir table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(table <- loaded) {

      table.records.foreach { record =>

        assert(record("Partition") == partition, error("Value unexpected: " + record("Partition")))
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

      partition = partition.plusDays(1)

    }
    println("Parted dir table loaded iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

    loaded.foreach(table => table.columns.foreach(column => column.clear))

    started = System.currentTimeMillis
    val mapped = map.dates(
      name,
      new LocalDate(millis),
      new LocalDate(millis).plusDays(parts)).tables
    println("Parted dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(table <- mapped) {

      table.records.foreach { record =>

        assert(record("Partition") == partition, error("Value unexpected: " + record("Partition")))
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

      partition = partition.plusDays(1)

    }
    println("Parted dir table mapped iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

