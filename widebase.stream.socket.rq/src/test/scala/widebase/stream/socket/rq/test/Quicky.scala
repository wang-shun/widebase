package widebase.stream.socket.rq.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import scala.collection.mutable.ArrayBuffer

import widebase.db.column. { DateTimeColumn, IntColumn, StringColumn }
import widebase.db.table.Table
import widebase.stream.handler.rq.RecordListener

/* A quicky test.
 *
 * @author myst3r10n
 */
object Quicky extends Logger with Loggable {

  import widebase.stream.socket.rq

  def main(args: Array[String]) {

    val amount = 10
    var received = 0
    val records = 100

    var i = 1
    class Listener extends RecordListener {
      var c = Int.box(i); i += 1
      def react = {

        case t: Table =>

          received += 1
          println(c + ": " + t)

      }
    }

    val producer = rq.producer
    var consumers = Array.fill(amount)(rq.consumer(new Listener))

    try {

      consumers.foreach(_.open.subscribe("quote"))

      producer.open
      for(i <- 1 to records)
        producer.publish("quote", Table(
          StringColumn("time", "bid", "ask", "symbol"),
          DateTimeColumn(LocalDateTime.now), IntColumn(i), IntColumn(i + 1), StringColumn("Share")))

      while(received < amount * records)
        Thread.sleep(100)

    } finally {

      producer.close
      consumers.foreach(_.close)

    }
  }
}

