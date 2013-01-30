package widebase.stream.socket.cq.test

import net.liftweb.common. { Loggable, Logger }

/* A server test.
 *
 * Run:
 *
 * test:run-main widebase.stream.socket.cq.test.Server -a etc/widebase-server/auths.properties
 *
 * @author myst3r10n
 */
object Server extends Logger with Loggable {

  import widebase.stream.socket.cq

  def main(args: Array[String]) {

    val server = cq.server

    var i = 0

    while(i < args.length) {

      args(i) match {

        case "-a" =>
          i += 1
          server.load(args(i))

        case "-f" =>
          i += 1
          server.filter(args(i))

        case "-p" =>
          i += 1
          server.port = args(i).toInt

        case _ =>
          error("Unfamiliar with argument: " + args(i))
          sys.exit(1)

      }

      i += 1

    }

    try {

      server.bind

      info("Listen on " + server.port)

      server.await

    } catch {

      case e =>
        e.printStackTrace
        sys.exit(1)

    } finally {

      server.close

    }
  }
}

