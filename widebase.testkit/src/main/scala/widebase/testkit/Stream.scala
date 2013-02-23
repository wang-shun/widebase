package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

/* Test all Stream units.
 *
 * @author myst3r10n
 */
object Stream extends Logger with Loggable {

  def main(args: Array[String]) {

    println("")
    println("// Socket")
    println("")
    Socket.main(args)

  }
}

