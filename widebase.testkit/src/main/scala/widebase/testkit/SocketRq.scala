package widebase.testkit

import java.io.IOException

import net.liftweb.common. { Loggable, Logger }

import widebase.db.table.Table
import widebase.stream.handler.AuthMap
import widebase.stream.handler.rq.RecordListener

/* A short publish-subscribe test.
 *
 * @author myst3r10n
 */
object SocketRq extends Logger with Loggable {

  import widebase.stream.socket.rq

  var arrived = false

  object listener extends RecordListener {

    def react = {

      case event: String => println("event: " + event)
      case (records: Int, partition: String) => println("rollback: " + records + " @ " + partition)
      case chunk: Table =>
        println("update: " + chunk)
        arrived = true

    }
  }

  def main(args: Array[String]) {

    // Authorization map
    val auths = new AuthMap {

      jaas = "widebase-broker"
      this += "FlushMessage" -> Array("admins", "producers")
      this += "NotifyMessage" -> Array("admins", "producers")
      this += "PublishMessage" -> Array("admins", "producers")
      this += "RemoteShutdownMessage" -> Array("admins")
      this += "SubscribeMessage" -> Array("admins", "consumers")
      this += "UnsubscribeMessage" -> Array("admins", "consumers")

    }

    val broker = rq.broker(null, auths)
    val consumer = rq.consumer(listener)
    val producer = rq.producer

    try {

      broker.bind
      consumer.login("consumer", "password").subscribe("table")
      producer.login("producer", "password").notify("table", "Hello World!")

      if(!arrived)
        Thread.sleep(100)

    } finally {

      producer.close
      consumer.close
      broker.close

    }
  }
}

