package widebase.stream.socket.test

import net.liftweb.common. { Loggable, Logger }

import widebase.stream.codec. { RequestDecoder, ResponseEncoder }
import widebase.stream.handler.AuthMap
import widebase.stream.socket.ServerLike

/* A server test.
 *
 * @author myst3r10n
 */
object Server extends Logger with Loggable {

  class Server extends ServerLike

  def main(args: Array[String]) {

    try {

      service

    } catch {

      case e =>
        e.printStackTrace
        sys.exit(1)

    }
  }

  def service {

    val server = new Server

    // Authorization map
    server.auths = new AuthMap {

      jaas = "widebase-server"
      this += "RemoteShutdownMessage" -> Array("admins")

    }

    try {

      // Listen port
      server.bind

      info("Listen on " + server.port)

      // Wait till closed
      server.await

    } catch {

      case e => throw e

    } finally {

      // Close and release resources
      server.close

    }
  }
}

