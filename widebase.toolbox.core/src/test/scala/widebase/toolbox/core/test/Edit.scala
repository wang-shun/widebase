package widebase.toolbox.core.test

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

import scala.swing.BorderPanel
import scala.util.Random

import widebase.db.table. { Table, TemplateTable }
import widebase.ui.swing.TablePopupMenu

/* Test visual table for table file, directory table and partitioned table.
 *
 * @author myst3r10n
 */
object Edit extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var parts: Int = _
  protected var records: Int = _

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init chart
  import widebase.toolbox.core.graphics._
  import widebase.toolbox.core.uitools._

  case class Data(
    val part: LocalDateTime,
    val bool: Boolean,
    val byte: Byte,
    val char: Char,
    val double: Double,
    val float: Float,
    val int: Int,
    val long: Long,
    val short: Short,
    val month: YearMonth,
    val date: LocalDate,
    val minute: Minutes,
    val second: Seconds,
    val time: LocalTime,
    val datetime: LocalDateTime,
    val timestamp: Timestamp,
    val symbol: Symbol,
    val string: String)

  case class DataTable(
    table: Table =
      Table(
        string(
          "part",
          "bool",
          "byte",
          "char",
          "double",
          "float",
          "int",
          "long",
          "short",
          "month",
          "date",
          "minute",
          "second",
          "time",
          "datetime",
          "timestamp",
          "symbol",
          "string"),
        datetime(),
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
        string()))
    extends TemplateTable[Data] {

    val part = table("part").Z
    val bool = table("bool").b
    val byte = table("byte").x
    val char = table("char").c
    val double = table("double").d
    val float = table("float").f
    val int = table("int").i
    val long = table("long").l
    val short = table("short").s
    val month = table("month").M
    val date = table("date").D
    val minute = table("minute").U
    val second = table("second").V
    val time = table("time").T
    val datetime = table("datetime").Z
    val timestamp = table("timestamp").P
    val symbol = table("symbol").Y
    val string = table("string").S

    def +=(data: Data) = {

      part += data.part
      bool += data.bool
      byte += data.byte
      char += data.char
      double += data.double
      float += data.double
      int += data.int
      long += data.long
      short += data.short
      month += data.month
      date += data.date
      minute += data.minute
      second += data.second
      time += data.time
      datetime += data.datetime
      timestamp += data.timestamp
      symbol += data.symbol
      string += data.string

      this

    }

    def +=(
      part: LocalDateTime,
      bool: Boolean,
      byte: Byte,
      char: Char,
      double: Double,
      float: Float,
      int: Int,
      long: Long,
      short: Short,
      month: YearMonth,
      date: LocalDate,
      minute: Minutes,
      second: Seconds,
      time: LocalTime,
      datetime: LocalDateTime,
      timestamp: Timestamp,
      symbol: Symbol,
      string: String): DataTable =
      this += Data(
        part,
        bool,
        byte,
        char,
        double,
        float,
        int,
        long,
        short,
        month,
        date,
        minute,
        second,
        time,
        datetime,
        timestamp,
        symbol,
        string)

    def ++=(table: DataTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) = Data(
      part(index),
      bool(index),
      byte(index),
      char(index),
      double(index),
      float(index),
      int(index),
      long(index),
      short(index),
      month(index),
      date(index),
      minute(index),
      second(index),
      time(index),
      datetime(index),
      timestamp(index),
      symbol(index),
      string(index))

    def filter(predicate: Data => Boolean) = {

      val filteredTable = new DataTable

      for(r <- 0 to records.length - 1)
        if(predicate(this(r)))
          filteredTable += this(r)

      filteredTable

    }

    def peer = table

  }

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

    var from = LocalDateTime.parse("2013-01-01 00:00:00.000", formatter)
    val till = LocalDateTime.parse("2013-02-01 00:00:00.000", formatter)

    saveTable("visualTable", fillTable(from, till))
    println("")
    saveDirTable("dirVisualTable", fillTable(from, till))
    println("")
    savePartedDirTable("dirVisualTable", fillTable(from, till))

    visualTable("visualTable")
    figure(2)
    tabbedTable(
      "dirVisualTable",
      "dirVisualTable", from.toLocalDate, till.toLocalDate) // partitioned

  }

  def fillTable(from: LocalDateTime, till: LocalDateTime) = {

    var started = 0L

    val table = new DataTable

    var records = 0
    var move = from

    started = System.currentTimeMillis
    while(move.compareTo(till) == -1) {

      try {

        move.toDateTime // Check illegal instant, see link below

        table.part += move
        table.bool += true
        table.byte += Byte.MaxValue
        table.char += Char.MaxValue
        table.double += Double.MaxValue
        table.float += Float.MaxValue
        table.int += Int.MaxValue
        table.long += Long.MaxValue
        table.short += Short.MaxValue
        table.month += new YearMonth(millis)
        table.date += new LocalDate(millis)
        table.minute += Minutes.minutes(12)
        table.second += Seconds.seconds(34)
        table.time += new LocalTime(millis)
        table.datetime += new LocalDateTime(millis)
        table.timestamp += new Timestamp(millis)
        table.symbol += 'Hello
        table.string += "World!"

      } catch {

        case e: IllegalArgumentException =>
          // NOTE: http://joda-time.sourceforge.net/faq.html#illegalinstant

      }

      move = move.plusHours(1)

    }
    info("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table

  }

  def saveTable(name: String, table: DataTable) {

    var started = 0L

    started = System.currentTimeMillis
    save(name, table.peer)
    info("Table saved " + table.records.length + " records in " +
      diff(started, System.currentTimeMillis))

    table.peer.columns.foreach(column => column.clear)

  }

  def saveDirTable(name: String, table: DataTable) {

    var started = 0L

    started = System.currentTimeMillis
    save.dir(name, table.peer)
    info("Dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.peer.columns.foreach(column => column.clear)

  }

  def savePartedDirTable(name: String, table: DataTable) {

    var started = 0L

    started = System.currentTimeMillis
    save.dir(name, table.peer)('daily)
    info("Parted dir table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table.peer.columns.foreach(column => column.clear)

  }

  def visualTable(name: String) {

    val tablePane = uitable(
      "Data",
      load(name),
      "UIContextMenu", new TablePopupMenu)

  }

  def tabbedTable(
    name1: String,
    name2: String, from: LocalDate, till: LocalDate) {

    val tabgroup = uitabgroup()

    val tab1 = uitab(tabgroup, "Title", "Directory Table")
    val tab2 = uitab(tabgroup, "Title", "Partitioned Table")

    val tablePane1 = uitable(
      tab1,
      "Data", map(name1),
      "UIContextMenu", new TablePopupMenu) // Default popup menu

    val tablePane2 = uitable(
      tab2,
      "Data", map.dates(name2, from, till),
      "UIContextMenu", new TablePopupMenu) // Default popup menu

  }
}

