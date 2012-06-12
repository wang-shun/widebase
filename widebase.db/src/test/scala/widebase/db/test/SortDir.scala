package widebase.db.test

import java.io.File
import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import widebase.db.column. {

  DoubleColumn,
  IntColumn,
  DateTimeColumn,
  StringColumn

}

import widebase.db.table.Table

/* Test of sort for directory tables.
 *
 * @author myst3r10n
 */
object SortDir extends Logger with Loggable {

  import widebase.db

  protected var debug: Boolean = _
  protected var label: String = _
  protected var method: Symbol = _
  protected var partition: String = _
  protected var segment: File = _
  protected var records: Int = _
  protected var table: String = _

  // Init DB
  val dbi =
    widebase.db.instance(System.getProperty("user.dir") + "/usr/data/test/db")

  // Init API
  import dbi.asSegmentPath
  import dbi.tables._

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    label = null
    partition = null
    segment = null
    records = 0
    table = null

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-l" =>
          i += 1
          label = args(i)

        case "-m" =>
          i += 1
          method = Symbol(args(i))

        case "-p" =>
          i += 1
          partition = args(i)

        case "-r" =>
          i += 1
          records = args(i).toInt

        case "-s" =>
          i += 1
          segment = args(i).S

        case "-t" =>
          i += 1
          table = args(i)

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    if(method != null && method != 'i && method != 's) {

      error("Unsupported sort method: " + method)
      sys.exit(1)

    }

    if(table == null) {

      error("No table set")
      sys.exit(1)

    }

    if(label == null) {

      error("No sort label set")
      sys.exit(1)

    }

    if(method == null || method == 'i)
      sort(table, label, 'i, records)(partition, segment)

    if(method == null)
      println("")

    if(method == null || method == 's)
      sort(table, label, 's, records)(partition, segment)

  }

  def sort(
    table: String,
    label: String,
    method: Symbol,
    records: Int = 0)
    (partition: String = null, segment: File = null) {

    var started = 0L

    started = System.currentTimeMillis
    val loaded = load.dir(table, records)(partition, segment)
    info("Table dir loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    if(debug)
      loaded.records.foreach(record => println(record))
    else {

      println("unsorted head: " + loaded.records.head)
      println("unsorted last: " + loaded.records.last)

    }

    started = System.currentTimeMillis
    db.table.sort(loaded, label, method, 'a)
    method match {
      case 'i =>
        info("Table insertion sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

      case 's =>
        info("Table selection sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

    }

    if(debug)
      loaded.records.foreach(record => println(record))
    else
      method match {

        case 'i =>
          println("insertion sort head: " + loaded.records.head)
          println("insertion sort last: " + loaded.records.last)

        case 's =>
          println("selection sort head: " + loaded.records.head)
          println("selection sort last: " + loaded.records.last)

      }

    loaded.columns.foreach(column => column.clear)

  }
}

