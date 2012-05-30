package widebase.db.test

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

/* Test of sort.
 *
 * @author myst3r10n
 */
object Sort extends Logger with Loggable {

  protected var debug: Boolean = _
  protected var method: Symbol = _
  protected var records: Int = _

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 100

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-m" =>
          i += 1
          method = Symbol(args(i))

        case "-r" =>
          i += 1
          records = args(i).toInt

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

    if(method == null || method == 'i)
      sort('i, records)

    if(method == null)
      println("")

    if(method == null || method == 's)
      sort('s, records)

  }

  def sort(method: Symbol, records: Int) {

    var started = 0L

    val table = Table(
      StringColumn(
        "time",
        "ask",
        "bid",
        "askVolume",
        "bidVolume",
        "instrument"),
      new DateTimeColumn,
      new DoubleColumn,
      new DoubleColumn,
      new DoubleColumn,
      new DoubleColumn,
      new IntColumn)

    val times = table("time").asInstanceOf[DateTimeColumn]
    val asks = table("ask").asInstanceOf[DoubleColumn]
    val bids = table("bid").asInstanceOf[DoubleColumn]
    val askVolumes = table("askVolume").asInstanceOf[DoubleColumn]
    val bidVolumes = table("bidVolume").asInstanceOf[DoubleColumn]
    val instruments = table("instrument").asInstanceOf[IntColumn]

    started = System.currentTimeMillis
    for(r <- 1 to records) {

      times += (new LocalDateTime(millis)).minusDays(r)
      asks += Float.MaxValue
      bids += Float.MinValue
      askVolumes += Float.MaxValue
      bidVolumes += Float.MinValue
      instruments += 0

    }
    info("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    if(debug)
      table.records.foreach(record => println(record))
    else {

      println("unsorted head: " + table.records.head)
      println("unsorted last: " + table.records.last)

    }

    started = System.currentTimeMillis
    table.sort("time", method, 'a)
    method match {
      case 'i =>
        info("Table insertion sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

      case 's =>
        info("Table selection sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

    }

    if(debug)
      table.records.foreach(record => println(record))
    else
      method match {

        case 'i =>
          println("insertion sort head: " + table.records.head)
          println("insertion sort last: " + table.records.last)

        case 's =>
          println("selection sort head: " + table.records.head)
          println("selection sort last: " + table.records.last)

      }

    table.columns.foreach(column => column.clear)

  }
}

