package widebase.toolbox.core.test

import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time. { LocalDate, LocalDateTime }
import org.joda.time.format.DateTimeFormat

import scala.swing.BorderPanel
import scala.util.Random

import widebase.db.table. { Table, TemplateTable }

/* Test XY chart for table file, directory table and partitioned table.
 *
 * @author myst3r10n
 */
object XY extends Logger with Loggable {

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
  import widebase.toolbox.core.graph2d._
  import widebase.toolbox.core.graphics._
  import widebase.toolbox.core.scribe._
  import widebase.toolbox.core.uitools._
  import scala.language.postfixOps

  case class Data(val x: Double, val y: Double)

  case class DataTable(
    table: Table = Table(string("x", "y"), double(), double()))
    extends TemplateTable[Data] {

    val x = table("x").d
    val y = table("y").d

    def +=(data: Data) = {

      x += data.x
      y += data.y

      this

    }

    def +=(x: Double, y: Double): DataTable =
      this += Data(x, y)

    def ++=(table: DataTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    def apply(index: Int) = Data(x(index), y(index))

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

    var from = 1
    val till = 100

    saveTable("plotXY", fillTable(from, till))
    println("")
    saveDirTable("dirPlotXY", fillTable(from, till))

    plotMixedTable("plotXY", "dirPlotXY")
    plotTabbedTable("plotXY", "dirPlotXY")

  }

  def fillTable(from: Int, till: Int) = {

    var started = 0L

    val table = new DataTable

    var records = 0
    var move = from

    started = System.currentTimeMillis
    for(i <- from to till) {

      table.x += i

      if(Random.nextBoolean)
        table.y += (math.random * 10).toInt
      else
        table.y += -(math.random * 10).toInt

      records += 1

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

  def plotMixedTable(name1: String, name2: String) {

    val table1 = DataTable(load(name1))
    val table2 = DataTable(map(name2))

    figure

    plot(
      table1.x, table1.y,
      table2.x, table2.y,
      "From", 0, "Till", 24)

    legend("Table File", "Directory Table")

    figure

    plot(
      table1.x, table1.y,
      "From", 0, "Till", 24)

    hold on

    plot(
      table2.x, table2.y,
      "From", 0, "Till", 24)

    hold off

    legend("Table File", "Directory Table")

  }

  def plotTabbedTable(name: String, dirName: String) {

    val table = DataTable(load(name))
    val dirTable = DataTable(map(dirName))

    figure

    val tabgroup = uitabgroup()

    val tab1 = uitab(tabgroup, "Title", "Table File")
    val tab2 = uitab(tabgroup, "Title", "Directory Table")

    val axes1 = axes(tab1)
    val axes2 = axes(tab2)

    plot(
      axes1,
      table.x,
      table.y,
      "From", 0, "Till", 24)

    legend(axes1, "Table File")

    plot(
      axes2,
      dirTable.x,
      dirTable.y,
      "From", 0, "Till", 24)

    legend(axes2, "Directory Table")

  }
}

