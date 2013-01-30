package widebase.stream.socket.rq.test

import java.io.IOException

import net.liftweb.common. { Loggable, Logger }

import widebase.db.table.Table
import widebase.stream.handler.rq.RecordListener

/* A short publish-subscribe test.
 *
 * @author myst3r10n
 */
object All extends Logger with Loggable {

  import widebase.stream.socket.rq

  object listener extends RecordListener {

    def react = {

      case event: String => println("event: " + event)
      case (records: Int, partition: String) => println("rollback: " + records + " @ " + partition)
      case chunk: Table => println("update: " + chunk)

    }
  }

  def main(args: Array[String]) {

    val broker = rq.broker
    val consumer = rq.consumer(listener)
    val producer = rq.producer

    try {

      broker.bind
      consumer.login("consumer", "password").subscribe("table")
      producer.login("producer", "password").notify("table", "Hello World!")

    } finally {

      producer.close
      consumer.close
      broker.close

    }
  }
}

