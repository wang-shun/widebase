package widebase.stream.socket.rq.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import widebase.db.column. { DateTimeColumn, IntColumn, StringColumn }
import widebase.db.table.Table
import widebase.stream.codec.rq.UnparsableMessage
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener

/* A selector test.
 *
 * @author myst3r10n
 */
object Selector extends Logger with Loggable {

  import widebase.stream.socket.rq

  def main(args: Array[String]) {

    var elapsed = 0
    val records = 100

    object Listener extends RecordListener {

      def react = {

        case t: Table =>

          elapsed = 0
          t.records.foreach(println(_))

      }
    }

    // Authorization map
    val auths = new AuthMap {

      jaas = "widebase-broker"
      this += "FlushMessage" -> Array("admins", "producers")
      this += "NotifyMessage" -> Array("admins", "producers")
      this += "PublishMessage" -> Array("admins", "producers")
      this += "RemoteShutdownMessage" -> Array("admins")
      this += "SelectorSupport" -> Array("admins", "consumers")
      this += "SubscribeMessage" -> Array("admins", "consumers")
      this += "UnsubscribeMessage" -> Array("admins", "consumers")

    }

    val broker = rq.broker(null, auths)
    val producer = rq.producer
    var consumer = rq.consumer(Listener)

    try {

      broker.bind

      consumer.login("consumer", "password").subscribe(
        "quote",
        """record("symbol") == "A" || record("symbol") == "C"""")

      producer.login("producer", "password")

      val symbols = Array("A", "B", "C")

      for(i <- 0 to records - 1)
        producer.publish(
          "quote",
          Table(
            StringColumn("time", "bid", "ask", "symbol"),
            DateTimeColumn(LocalDateTime.now),
            IntColumn(i + 1),
            IntColumn(i + 2),
            StringColumn(symbols(i % symbols.size))))

      while(elapsed < 3) {

        elapsed += 1
        Thread.sleep(1000)

      }
    } catch {

      case e: UnparsableMessage => error("Unparsable selector")

    } finally {

      producer.close
      consumer.close
      broker.close

    }
  }
}

