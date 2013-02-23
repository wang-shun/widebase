package widebase.testkit.test

import net.liftweb.common. { Loggable, Logger }

/* Test all Stream/RQ units.
 *
 * @author myst3r10n
 */
object StreamRq extends Logger with Loggable {

  def main(args: Array[String]) {

    SocketRq.main(args)
    println("")
    PubSub.main(args)
    println("")
    Selector.main(args)

  }
}

