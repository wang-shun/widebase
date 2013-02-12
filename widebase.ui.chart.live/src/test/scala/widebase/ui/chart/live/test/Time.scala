package widebase.ui.chart.live.test

import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { LocalDate, LocalDateTime }
import org.joda.time.format.DateTimeFormat

import scala.util.Random

import widebase.db.table. { Table, TemplateTable }

/* Test time chart for table file, directory table and partitioned table.
 *
 * @author myst3r10n
 */
object Time extends Logger with Loggable {

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
  import widebase.ui.chart.live._

  case class Data(val time: LocalDateTime, val value: Double)

  case class DataTable(
    table: Table = Table(string("time", "value"), dateTime(), double()))
    extends TemplateTable[Data] {

    val time = table("time").Z
    val value = table("value").d

    def +=(data: Data) = {

      time += data.time
      value += data.value

      this

    }

    def +=(time: LocalDateTime, value: Double): DataTable =
      this += Data(time, value)

    def ++=(table: DataTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) = Data(time(index), value(index))

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

    saveTable("plotTime", fillTable(from, till))
    println("")
    saveDirTable("dirPlotTime", fillTable(from, till))
    println("")
    savePartedDirTable("dirPlotTime", fillTable(from, till))

    plotTable("plotTime")
    figure += 1
    plotDirTable("dirPlotTime")
    figure += 1
    plotPartedDirTable("dirPlotTime", from.toLocalDate, till.toLocalDate)

    figure += 1

    plotMixedTable(
      "plotTime",
      "dirPlotTime",
      "dirPlotTime",
      from.toLocalDate,
      till.toLocalDate)

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

        if(Random.nextBoolean)
          table.value += (math.random * 10).toInt
        else
          table.value += -(math.random * 10).toInt

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

    plot(table.time, table.value, "from", 0, "till", 24, ";Table File;")

  }

  def plotDirTable(name: String) {

    val table = DataTable(map(name))

    plot(table.time, table.value, "from", 0, "till", 24, ";Directory Table;")

  }

  def plotPartedDirTable(name: String, from: LocalDate, till: LocalDate) {

    var started = 0L

    started = System.currentTimeMillis
    val parts = map.dates(name, from, till)
    info("Parted dir table mapped " + parts.size + " tables in " +
      diff(started, System.currentTimeMillis))

    plot(parts.tables("time").Zia, parts.tables("value").dia, "from", 0, "till", 24, ";Partitioned Table;")

  }

  def plotMixedTable(
    name: String,
    dirName: String,
    partedName: String,
    from: LocalDate,
    till: LocalDate) {

    val table = DataTable(load(name))
    val dirTable = DataTable(map(dirName))
    val parts = map.dates(partedName, from, till)

    plot(
      table.time, table.value, "from", 0, "till", 24, ";Table File;",
      dirTable.time, dirTable.value, ";Directory Table;",
      parts.tables("time").Zia, parts.tables("value").dia, ";Partitioned Table;")

  }
}

