package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

/* Test all Stream/RQ units.
 *
 * @author myst3r10n
 */
object StreamRq extends Logger with Loggable {

  def main(args: Array[String]) {

    println("")
    println("// Socket/RQ")
    println("")
    SocketRq.main(args)
    println("")
    println("// Pub/Sub (Socket/RQ)")
    println("")
    PubSub.main(args)
    println("")
    println("// Selector (Socket/RQ)")
    println("")
    Selector.main(args)

  }
}

