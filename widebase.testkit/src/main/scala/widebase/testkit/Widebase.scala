package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

/* Test all Widebase units.
 *
 * @author myst3r10n
 */
object Widebase extends Logger with Loggable {

  def main(args: Array[String]) {

    io(args)
    println("")
    core(args)
    println("")
    dsl(args)
    println("")
    performance(args)

  }
}

