package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

import widebase.db.table.Table

/* Test of sort.
 *
 * @author myst3r10n
 */
object Sort extends Logger with Loggable {

  import widebase.db

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  // Init Testkit
  import widebase.testkit._

  protected var method: Symbol = _
  protected var records: Int = _

  val millis = LocalDateTime.parse(
    "2012-01-23 12:34:56.789",
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toDateTime.getMillis

  def main(args: Array[String]) {

    records = 100

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

  def fill(records: Int) = {

    var started = 0L

    val table = Table(
      string(
        "time",
        "ask",
        "bid",
        "askVolume",
        "bidVolume",
        "instrument"),
      dateTime(),
      double(),
      double(),
      double(),
      double(),
      int())

    val times = table("time").Z
    val asks = table("ask").d
    val bids = table("bid").d
    val askVolumes = table("askVolume").d
    val bidVolumes = table("bidVolume").d
    val instruments = table("instrument").i

    started = System.currentTimeMillis
    for(r <- 1 to records) {

      times += (new LocalDateTime(millis)).minusDays(r)
      asks += Float.MaxValue
      bids += Float.MinValue
      askVolumes += Float.MaxValue
      bidVolumes += Float.MinValue
      instruments += 0

    }
    println("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    table

  }

  def sort(method: Symbol, records: Int) {

    var started = 0L

    val origin = fill(records)
    val sorted = fill(records)

    started = System.currentTimeMillis
    db.table.sort(sorted, "time", method, 'a)
    method match {
      case 'i =>
        println("Table insertion sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

      case 's =>
        println("Table selection sort " + records + " records in " +
          diff(started, System.currentTimeMillis))

    }

    for(r <- 0 to records - 1) {

      assert(origin("time")(r) == sorted("time")(records - 1 - r), error("Value unexpected: " + sorted("time")(records - 1 - r)))
      assert(origin("ask")(r) == sorted("ask")(records - 1 - r), error("Value unexpected: " + sorted("ask")(records - 1 - r)))
      assert(origin("bid")(r) == sorted("bid")(records - 1 - r), error("Value unexpected: " + sorted("bid")(records - 1 - r)))
      assert(origin("askVolume")(r) == sorted("askVolume")(records - 1 - r), error("Value unexpected: " + sorted("askVolume")(records - 1 - r)))
      assert(origin("bidVolume")(r) == sorted("bidVolume")(records - 1 - r), error("Value unexpected: " + sorted("bidVolume")(records - 1 - r)))
      assert(origin("instrument")(r) == sorted("instrument")(records - 1 - r), error("Value unexpected: " + sorted("instrument")(records - 1 - r)))

    }

    origin.columns.foreach(column => column.clear)
    sorted.columns.foreach(column => column.clear)

  }
}

