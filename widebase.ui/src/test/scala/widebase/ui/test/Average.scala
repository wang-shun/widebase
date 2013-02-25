package widebase.ui.test

import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { LocalDate, LocalDateTime }
import org.joda.time.format.DateTimeFormat

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

import widebase.db.column. { DateTimeColumn, DoubleColumn }
import widebase.db.table. { Table, TemplateTable }

/* Test overlay of mixed charts.
 *
 * @author myst3r10n
 */
object Average extends Logger with Loggable {

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
  import widebase.ui._

  case class Data(
    val time: LocalDateTime,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double)

  case class DataTable(
    table: Table = Table(
      string("time", "open", "high", "low", "close"),
      datetime(),
      double(),
      double(),
      double(),
      double()))
    extends TemplateTable[Data] {

    val time = table("time").Z
    val open = table("open").d
    val high = table("high").d
    val low = table("low").d
    val close = table("close").d

    def +=(data: Data) = {

      time += data.time
      open += data.open
      high += data.high
      low += data.low
      close += data.close

      this

    }

    def +=(
      time: LocalDateTime,
      open: Double,
      high: Double,
      low: Double,
      close: Double): DataTable =
      this += Data(time, open, high, low, close)

    def ++=(table: DataTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) =
      Data(time(index), open(index), high(index), low(index), close(index))

    def filter(predicate: Data => Boolean) = {

      val filteredTable = new DataTable

      for(r <- 0 to records.length - 1)
        if(predicate(this(r)))
          filteredTable += this(r)

      filteredTable

    }

    def peer = table

  }

  case class Average(val time: LocalDateTime, val value: Double)

  case class AverageTable(
    table: Table = Table(string("time", "value"), datetime(), double()))
    extends TemplateTable[Average] {

    val time = table("time").Z
    val value = table("value").d

    def +=(data: Average) = {

      time += data.time
      value += data.value

      this

    }

    def +=(time: LocalDateTime, value: Double): AverageTable =
      this += Average(time, value)

    def ++=(table: AverageTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) = Average(time(index), value(index))

    def filter(predicate: Average => Boolean) = {

      val filteredTable = new AverageTable

      for(r <- 0 to records.length - 1)
        if(predicate(this(r)))
          filteredTable += this(r)

      filteredTable

    }

    def peer = table

  }

  val useCandle = false // Enable candlestick renderer

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

    var from = LocalDateTime.parse("2013-01-01 00:00:00.000", formatter)
    val till = LocalDateTime.parse("2013-02-01 00:00:00.000", formatter)

    saveTable("plotHighlow", fillTable(from, till))
    println("")
    saveDirTable("dirPlotHighlow", fillTable(from, till))
    println("")
    savePartedDirTable("dirPlotHighlow", fillTable(from, till))

    plotTable("plotHighlow")
    figure += 1
    plotDirTable("dirPlotHighlow")
    figure += 1
    plotPartedDirTable("dirPlotHighlow", from.toLocalDate, till.toLocalDate)

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

        table.time += move

        if(table.open.length == 0)
          table.open += 10 + (math.random * 10).toInt
        else
          table.open += table.close.last

        table.close += 10 + (math.random * 10).toInt

        if(table.open.last < table.close.last) {

          table.high += table.close.last + (math.random * 5).toInt
          table.low += table.open.last - (math.random * 5).toInt

        } else {

          table.high += table.open.last + (math.random * 5).toInt
          table.low += table.close.last - (math.random * 5).toInt

        }

        records += 1

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

  def plotTable(name: String) {

    val table = DataTable(load(name))

    if(useCandle)
      candle(
        table.high,
        table.low,
        table.close,
        table.open,
        table.time,
        "from", 0, "till", 24, ";Table File;")
    else
      highlow(
        table.high,
        table.low,
        table.close,
        table.open,
        table.time,
        "from", 0, "till", 24, ";Table File;")

    val period = 14

    def average(record: Int) = {

      if(record < period)
        Double.NaN
      else {

        var value = 0.0

        for(r <- record - period to record)
          value += table.close(r)

        value / period

      }
    }

    hold = true

    plot(table.time, func(average), ";Average;")

    hold = false

  }

  def plotDirTable(name: String) {

    val table = DataTable(map(name))

    if(useCandle)
      candle(
        table.high,
        table.low,
        table.close,
        table.open,
        table.time,
        "from", 0, "till", 24, ";Directory Table;")
    else
      highlow(
        table.high,
        table.low,
        table.close,
        table.open,
        table.time,
        "from", 0, "till", 24, ";Directory Table;")

    val period = 14

    def average(record: Int) = {

      if(record < period)
        Double.NaN
      else {

        var value = 0.0

        for(r <- record - period + 1 to record)
          value += table.close(r)

        value / period

      }
    }

    hold = true

    plot(table.time, func(average), ";Average;")

    hold = false

  }

  def plotPartedDirTable(name: String, from: LocalDate, till: LocalDate) {

    var started = 0L

    started = System.currentTimeMillis
    val parts = map.dates(name, from, till)
    info("Parted dir table mapped " + parts.size + " tables in " +
      diff(started, System.currentTimeMillis))

    if(useCandle)
      candle(
        parts.tables("high").dia,
        parts.tables("low").dia,
        parts.tables("close").dia,
        parts.tables("open").dia,
        parts.tables("time").Zia,
        "from", 0, "till", 24, ";Partitioned Table;")
    else
      highlow(
        parts.tables("high").dia,
        parts.tables("low").dia,
        parts.tables("close").dia,
        parts.tables("open").dia,
        parts.tables("time").Zia,
        "from", 0, "till", 24, ";Partitioned Table;")

    val tables =
      (for(table <- parts.tables)
        yield(DataTable(table))).toArray

    val period = 14

    def average(part: Int, record: Int): Number = {

      var p = part
      var index = 0
      var value = 0.0

      while(p >= 0) {

        var r =
          if(p == part)
            record
          else
            tables(p).records.length - 1

        while(r >= 0) {

          value += tables(p).close(r)

          r -= 1
          index += 1

          if(index == period)
            return value / period

        }

        p -= 1

      }

      Double.NaN

    }

    hold = true

    plot(parts.tables("time").Zia, funcp(average), ";Average;")

    hold = false

  }
}

