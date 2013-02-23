package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

/* Test all Stream/CQ units.
 *
 * @author myst3r10n
 */
object StreamCq extends Logger with Loggable {

  def main(args: Array[String]) {

    println("")
    println("// Socket/CQ")
    println("")
    SocketCq.main(args)
    println("")
    println("// Cache (Socket/CQ)")
    println("")
    Cache.main(args)

  }
}

