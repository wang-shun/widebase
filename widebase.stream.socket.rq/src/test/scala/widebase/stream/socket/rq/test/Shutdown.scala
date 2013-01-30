package widebase.stream.socket.rq.test

import net.liftweb.common. { Loggable, Logger }

/* A remote shutdown test.
 *
 * @author myst3r10n
 */
object Shutdown extends Logger with Loggable {

  import widebase.stream.socket.rq

  def main(args: Array[String]) {

    val producer = rq.producer

    try {

      producer.login("admin", "password").remoteShutdown

    } finally {

      producer.close

    }
  }
}

