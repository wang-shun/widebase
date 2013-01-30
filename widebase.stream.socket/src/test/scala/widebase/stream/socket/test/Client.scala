package widebase.stream.socket.test

import java.io.IOException

import net.liftweb.common. { Loggable, Logger }

import widebase.stream.codec. { LengthDecoder, LengthEncoder }
import widebase.stream.codec. { RequestEncoder, ResponseDecoder }
import widebase.stream.socket. { ClientLike, LoginLike, RemoteShutdownLike }

/* A client test.
 *
 * @author myst3r10n
 */
object Client extends Logger with Loggable {

  class Client extends ClientLike with LoginLike with RemoteShutdownLike {

    pipeline += "lengthDecoder" -> new LengthDecoder
    pipeline += "responseDecoder" -> new ResponseDecoder
    pipeline += "lengthEncoder" -> new LengthEncoder
    pipeline += "requestEncoder" -> new RequestEncoder

  }

  def main(args: Array[String]) {

    for(i <- 1 to 3)
      try {

        authentication

      } catch {

        case e =>
          e.printStackTrace
          sys.exit(1)

      }

    try {

      remoteShutdown

    } catch {

      case e =>
        e.printStackTrace
        sys.exit(1)

    }
  }

  def authentication {

    val client = new Client

    try {

      client.open
      client.login("client", "password")

    } catch {

      case e => throw e

    } finally {

      client.close

    }
  }

  def remoteShutdown {

    val client = new Client

    try {

      client.open
      client.login("admin", "password")
      client.remoteShutdown

    } catch {

      case e => throw e

    } finally {

      client.close

    }
  }
}

