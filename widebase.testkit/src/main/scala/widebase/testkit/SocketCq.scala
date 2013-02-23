package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

/* A short client/server test.
 *
 * @author myst3r10n
 */
object SocketCq extends Logger with Loggable {

  import widebase.stream.socket.cq

  def main(args: Array[String]) {

    val client = cq.client
    val server = cq.server

    try {

      server.bind

      client
        .login("client", "password").close
        .login("admin", "password").remoteShutdown

      server.await

    } finally {

      client.close
      server.close

    }
  }
}

