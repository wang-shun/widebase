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

/* Test of load, map and save operations with native columns on segmented table.
 *
 * @author myst3r10n
 */
object Segment extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.asSegmentPath
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

  protected var parts: Int = _
  protected var records: Int = _

  def main(args: Array[String]) {

    parts = 10
    records = 25000

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
    save(name, table)("test".S)
    println("Table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load(name)("test".S)
    println("Table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    boolCol = loaded("Bool").b
    byteCol = loaded("Byte").x
    charCol = loaded("Char").c
    doubleCol = loaded("Double").d
    floatCol = loaded("Float").f
    intCol = loaded("Int").i
    longCol = loaded("Long").l
    shortCol = loaded("Short").s
    monthCol = loaded("Month").M
    dateCol = loaded("Date").D
    minuteCol = loaded("Minute").U
    secondCol = loaded("Second").V
    timeCol = loaded("Time").T
    dateTimeCol = loaded("DateTime").Z
    timestampCol = loaded("Timestamp").P
    symbolCol = loaded("Symbol").Y
    stringCol = loaded("String").S

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
    println("Dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)(null, "test".S)
    println("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load.dir(name)(null, "test".S)
    println("Dir table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    boolCol = loaded("Bool").b
    byteCol = loaded("Byte").x
    charCol = loaded("Char").c
    doubleCol = loaded("Double").d
    floatCol = loaded("Float").f
    intCol = loaded("Int").i
    longCol = loaded("Long").l
    shortCol = loaded("Short").s
    monthCol = loaded("Month").M
    dateCol = loaded("Date").D
    minuteCol = loaded("Minute").U
    secondCol = loaded("Second").V
    timeCol = loaded("Time").T
    dateTimeCol = loaded("DateTime").Z
    timestampCol = loaded("Timestamp").P
    symbolCol = loaded("Symbol").Y
    stringCol = loaded("String").S

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
    println("Dir table loaded iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

    loaded.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val mapped = map(name)(null, "test".S)
    println("Dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    boolCol = mapped("Bool").b
    byteCol = mapped("Byte").x
    charCol = mapped("Char").c
    doubleCol = mapped("Double").d
    floatCol = mapped("Float").f
    intCol = mapped("Int").i
    longCol = mapped("Long").l
    shortCol = mapped("Short").s
    monthCol = mapped("Month").M
    dateCol = mapped("Date").D
    minuteCol = mapped("Minute").U
    secondCol = mapped("Second").V
    timeCol = mapped("Time").T
    dateTimeCol = mapped("DateTime").Z
    timestampCol = mapped("Timestamp").P
    symbolCol = mapped("Symbol").Y
    stringCol = mapped("String").S

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
    println("Dir table iterated " + records + " records in " +
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
        "String"),
      date(),
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

    var partitionCol = table("Partition").D
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

    var partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(p <- 1 to parts) {

      for(r <- 1 to records / parts) {

        partitionCol += partition
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

      partition = partition.plusDays(1)

    }
    println("Parted dir table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    save.dir(name, table)('daily, "test".S)
    println("Parted dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = load.dates(
      name,
      new LocalDate(millis),
      new LocalDate(millis).plusDays(parts))("test".S).tables
    println("Parted dir table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(table <- loaded) {

      partitionCol = table("Partition").D
      boolCol = table("Bool").b
      byteCol = table("Byte").x
      charCol = table("Char").c
      doubleCol = table("Double").d
      floatCol = table("Float").f
      intCol = table("Int").i
      longCol = table("Long").l
      shortCol = table("Short").s
      monthCol = table("Month").M
      dateCol = table("Date").D
      minuteCol = table("Minute").U
      secondCol = table("Second").V
      timeCol = table("Time").T
      dateTimeCol = table("DateTime").Z
      timestampCol = table("Timestamp").P
      symbolCol = table("Symbol").Y
      stringCol = table("String").S

      for(r <- 0 to records / parts - 1) {

        assert(partitionCol(r) == partition, error("Value unexpected: " + partitionCol(r)))
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

      partition = partition.plusDays(1)

    }
    println("Parted dir table loaded iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

    loaded.foreach(table => table.columns.foreach(column => column.clear))

    started = System.currentTimeMillis
    val mapped = map.dates(
      name,
      new LocalDate(millis),
      new LocalDate(millis).plusDays(parts))("test".S).tables
    println("Parted dir table mapped " + records + " records in " +
      diff(started, System.currentTimeMillis))

    partition = new LocalDate(millis)

    started = System.currentTimeMillis
    for(table <- mapped) {

      partitionCol = table("Partition").D
      boolCol = table("Bool").b
      byteCol = table("Byte").x
      charCol = table("Char").c
      doubleCol = table("Double").d
      floatCol = table("Float").f
      intCol = table("Int").i
      longCol = table("Long").l
      shortCol = table("Short").s
      monthCol = table("Month").M
      dateCol = table("Date").D
      minuteCol = table("Minute").U
      secondCol = table("Second").V
      timeCol = table("Time").T
      dateTimeCol = table("DateTime").Z
      timestampCol = table("Timestamp").P
      symbolCol = table("Symbol").Y
      stringCol = table("String").S

      for(r <- 0 to records / parts - 1) {

        assert(partitionCol(r) == partition, error("Value unexpected: " + partitionCol(r)))
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

      partition = partition.plusDays(1)

    }
    println("Parted dir table mapped iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

