package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

/* Test all Stream/CQ units.
 *
 * @author myst3r10n
 */
object StreamCq extends Logger with Loggable {

  def main(args: Array[String]) {

    SocketCq.main(args)
    println("")
    Cache.main(args)

  }
}

