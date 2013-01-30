package widebase.stream.socket.cq.test

import net.liftweb.common. { Loggable, Logger }

/* A remote shutdown test.
 *
 * @author myst3r10n
 */
object Shutdown extends Logger with Loggable {

  import widebase.stream.socket.cq

  protected var filter: String = _

  def main(args: Array[String]) {

    val client = cq.client

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-f" =>
          i += 1
          client.filter(args(i))

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    try {

      client.login("admin", "password").remoteShutdown

    } catch {

      case e =>
        e.printStackTrace
        return

    } finally {

      client.close

    }
  }
}

