package widebase.stream.socket.rq.test

import org.joda.time.LocalDateTime

import net.liftweb.common. { Loggable, Logger }

import scala.concurrent.Lock
import scala.util.Random

import widebase.db.table. { Table, TemplateTable }
import widebase.stream.handler.rq.RecordListener

/* A short publish-subscribe example.
 *
 * @author myst3r10n
 */
object Example extends Logger with Loggable {

  import scala.actors.Futures.future
  import widebase.stream.socket.rq

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  /** A record. */
  case class Quote(
    val time: LocalDateTime,
    val bid: Double,
    val ask: Double,
    val bidSize: Int,
    val askSize: Int,
    val symbol: String)

  /** A table based on `Quote` record. */
  case class QuoteTable(
    table: Table = Table(
      string("time", "bid", "ask", "bidSize", "askSize", "symbol"),
      dateTime(),
      double(),
      double(),
      int(),
      int(),
      string()))
    extends TemplateTable[Quote] {

    /** Time column. */
    val time = table("time").Z

    /** Bid column. */
    val bid = table("bid").d

    /** Ask column. */
    val ask = table("ask").d

    /** Bid size column. */
    val bidSize = table("bidSize").i

    /** Ask size column. */
    val askSize = table("askSize").i

    /** Symbol column. */
    val symbol = table("symbol").S

    /** Add a record by `Quote` record. */
    def +=(quote: Quote) = {

      time += quote.time
      bid += quote.bid
      ask += quote.ask
      bidSize += quote.bidSize
      askSize += quote.askSize
      symbol += quote.symbol

      this

    }

    /** Add a record by each record values. */
    def +=(
      time: LocalDateTime,
      bid: Double,
      ask: Double,
      bidSize: Int,
      askSize: Int,
      symbol: String): QuoteTable =
      this += Quote(time, bid, ask, bidSize, askSize, symbol)

    /** Add one or more records by another `QuoteTable`. */
    def ++=(table: QuoteTable) = {

      for(r <- 0 to table.records.length - 1)
        this += table(r)

      this

    }

    /** Get a record by index. */
    def apply(index: Int) = Quote(
      time(index),
      bid(index),
      ask(index),
      bidSize(index),
      askSize(index),
      symbol(index))

    /** Filter records by `predicate`. */
    def filter(predicate: Quote => Boolean) = {

      val filteredTable = new QuoteTable

      for(r <- 0 to records.length - 1)
        if(predicate(this(r)))
          filteredTable += this(r)

      filteredTable

    }

    /** Get underlying `Table`. */
    def peer = table

  }

  def main(args: Array[String]) {

    val lock = new Lock

    var count = 1
    var elapsed = 0
    var reacted = 0

    val publish = 100
    val subscribers = 10

    class Listener extends RecordListener {

      var number = Int.box(count);
      count += 1

      def react = {

        case t: Table =>

          elapsed = 0
          lock.acquire
          reacted += 1
          lock.release

          // Print all received quotes.
          QuoteTable(t).foreach { table =>

            println("listener " + number + ": " +
              table.time + " " + table.symbol + " " +
              table.bid + " < " + table.ask + " by " +
              table.bidSize + " and " + table.askSize)

          }
      }
    }

    val producer = rq.producer
    var consumers = Array.fill(subscribers)(rq.consumer(new Listener))

    try {

      consumers.foreach(consumer =>
        consumer.open.subscribe("quote", """record("symbol") != "B""""))

      future {

        /** Listed shares. */
        val listed = Array("A", "B", "C")

        producer.open

        for(i <- 0 to publish - 1)
          producer.publish("quote", quote(listed, i).peer)

      }

      while(reacted < publish * subscribers)
        Thread.sleep(100)

    } finally {

      producer.close
      consumers.foreach(_.close)

    }
  }

  /** Create 10 quotes at once. */
  def quote(listed: Array[String], price: Double) = {

    val table = new QuoteTable

    for(i <- 0 to 9) {

      table.time += LocalDateTime.now
      table.bid += price + math.abs(i.toDouble / 10)
      table.ask += (BigDecimal(table.bid.last) + 0.1).toDouble
      table.bidSize += math.abs(Random.nextFloat * 1000).toInt
      table.askSize += math.abs(Random.nextFloat * 1000).toInt
      table.symbol += listed(i % listed.size)

    }

    table

  }
}

