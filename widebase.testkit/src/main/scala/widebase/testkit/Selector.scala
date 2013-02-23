package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDateTime

import scala.concurrent.Lock

import widebase.db.table.Table
import widebase.stream.codec.rq.UnparsableMessage
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener
import widebase.stream.socket.rq. { Broker, Consumer }

/* A selector test.
 *
 * @author myst3r10n
 */
object Selector extends Logger with Loggable {

  import widebase.stream.socket.rq

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  val lock = new Lock
  var broker: Broker = _
  var consumer: Consumer = _

  def main(args: Array[String]) {

    var await = 0
    val records = 100

    object Listener extends RecordListener {

      def react = {

        case chunk: Table =>

          try {

            lock.acquire

            chunk("symbol").foreach { symbol =>

              await -= 1
              assert(Array("A", "C").contains(symbol), error("Value unexpected: " + symbol))

            }

            if(await == 0) {

              consumer.close
              broker.close

            }
          } finally {

            lock.release

          }

        case unparser: UnparsableMessage => error(unparser.reason)

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

    broker = rq.broker(null, auths)
    val producer = rq.producer
    consumer = rq.consumer(Listener)

    try {

      broker.bind

      info("Listen on " + broker.port)

      consumer.login("consumer", "password").subscribe(
        "quote",
        """record("symbol") == "A" || record("symbol") == "C"""")

      producer.login("producer", "password")

      val symbols = Array("A", "B", "C")

      for(i <- 0 to records - 1) {

        val symbol = symbols(i % symbols.size)

        if(Array("A", "B").contains(symbol))
          await += 1

        producer.publish(
          "quote",
          Table(
            string("time", "bid", "ask", "symbol"),
            datetime(LocalDateTime.now),
            int(i + 1),
            int(i + 2),
            string(symbol)))

      }

      // Required only in association with All.scala regarding async
      var done = false
      do {

        try {

          lock.acquire
          done = await == 0

        } finally {

          lock.release

        }

        if(!done)
          Thread.sleep(1000)

      } while(!done)
    } catch {

      case e: UnparsableMessage => error("Unparsable selector")

    } finally {

      producer.close

      try {

        lock.acquire

        if(await == 0) {

          consumer.close
          broker.close

        }

      } finally {

        lock.release

      }
    }
  }
}

