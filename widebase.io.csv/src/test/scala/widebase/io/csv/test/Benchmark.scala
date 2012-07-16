package widebase.io.csv.test

import java.io.File

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import widebase.db.table.Table

/** A small benchmark for importers.
 *
 * @author myst3r10n
 */
object Benchmark extends Logger with Loggable {

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.tables._
  import widebase.io.csv
  import widebase.io.csv.filter

  val debug = false

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    var started = 0L

    started = System.currentTimeMillis
    val columns = csv.columns(
      "bxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      "usr/csv/columns.csv.gz",
      "g")
    println("Columns imported with " + columns.head.length + " length in " +
      diff(started, System.currentTimeMillis))

    println("")

    started = System.currentTimeMillis
    val table = csv.table(
      "bxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      "usr/csv/table.csv.gz",
      "g")
    println("Table imported with " + table.records.length + " records in " +
      diff(started, System.currentTimeMillis))

    println("")

    started = System.currentTimeMillis
    csv.table.to(
      dbi.path,
      "table",
      "DbxcdfilsMDUVTZPYS",
      ",",
      filter.none,
      "usr/csv/partitionedTable.csv.gz",
      "g")('daily)
    println("Table transfered in " + diff(started, System.currentTimeMillis))

  }
}

