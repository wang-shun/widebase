package widebase.stream.socket.cq.test

import java.text.SimpleDateFormat

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import widebase.db.column. {

  DoubleColumn,
  DateTimeColumn,
  StringColumn

}

import widebase.db.table.Table
import widebase.stream.socket.cq.Client

/** Test rollback scenario.
 *
 * @author myst3r10n
 */
object Rollback extends Logger with Loggable {

  import widebase.stream.socket.cq

  protected var debug = false
  protected var records: Int = _
  protected var client: Client = _

  val millis = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S")
    .parse("2012-01-23 12:34:56.789").getTime

  def diff(started: Long, stopped: Long) =
    "%.3f s".format((stopped - started).toDouble / 1000)

  def main(args: Array[String]) {

    debug = false
    records = 25000

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-d" => debug = true

        case "-r" =>
          i += 1
          records = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    client = cq.client("localhost", 50000)

    try {

      client.login("client", "password")

      quote("quote", records)

    } catch {

      case e =>
        e.printStackTrace
        sys.exit(1)

    } finally {

      client.close

    }
  }

  def quote(name: String, records: Int) {

    var started = 0L

    val quote = Table(StringColumn(
      "Time",
      "Ask",
      "Bid",
      "AskVolume",
      "BidVolume",
      "symbol"),
      new DateTimeColumn,
      new DoubleColumn,
      new DoubleColumn,
      new DoubleColumn,
      new DoubleColumn,
      new StringColumn)

    var times = quote("Time").asInstanceOf[DateTimeColumn]
    var asks = quote("Ask").asInstanceOf[DoubleColumn]
    var bids = quote("Bid").asInstanceOf[DoubleColumn]
    var askVolumes = quote("AskVolume").asInstanceOf[DoubleColumn]
    var bidVolumes = quote("BidVolume").asInstanceOf[DoubleColumn]
    var instruments = quote("symbol").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 1 to records) {

      times += new LocalDateTime(millis)
      asks += 1.2011
      bids += 1.2009
      askVolumes += 1.1
      bidVolumes += 2.2
      instruments += "EUR/CHF"

    }
    info("Table filled " + records + " records in " +
      diff(started, System.currentTimeMillis))

    started = System.currentTimeMillis
    client.save(name, quote)
    info("Table saved " + records + " records in " +
      diff(started, System.currentTimeMillis))

    quote.columns.foreach(column => column.clear)

    started = System.currentTimeMillis
    val loaded = client.load(name)
    info("Table loaded " + records + " records in " +
      diff(started, System.currentTimeMillis))

    times = loaded("Time").asInstanceOf[DateTimeColumn]
    asks = loaded("Ask").asInstanceOf[DoubleColumn]
    bids = loaded("Bid").asInstanceOf[DoubleColumn]
    askVolumes = loaded("askVolume").asInstanceOf[DoubleColumn]
    bidVolumes = loaded("BidVolume").asInstanceOf[DoubleColumn]
    instruments = loaded("symbol").asInstanceOf[StringColumn]

    started = System.currentTimeMillis
    for(r <- 0 to records - 1)
      if(debug || r == records - 1) {

        println("Time: " + times(r))
        println("Ask: " + asks(r))
        println("Bid: " + bids(r).toInt + " (as Int)")
        println("AskVolume: " + askVolumes(r))
        println("BidVolume: " + bidVolumes(r))
        println("symbol: " + instruments(r))

      } else {

        times(r)
        asks(r)
        bids(r)
        askVolumes(r)
        bidVolumes(r)
        instruments(r)

      }

    info("Table iterated " + records + " records in " +
      diff(started, System.currentTimeMillis))

  }
}

