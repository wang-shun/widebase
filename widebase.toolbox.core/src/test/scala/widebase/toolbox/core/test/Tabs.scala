package widebase.toolbox.core.test

import net.liftweb.common. { Loggable, Logger }

/* Test tabs for multiple plots.
 *
 * @author myst3r10n
 */
object Tabs extends Logger with Loggable {

  import widebase.toolbox.core.uitools._

  def main(args: Array[String]) {

    val group = uitabgroup()
    val tab1 = uitab(group, "Title", "Tab 1")
    val tab2 = uitab(group, "Title", "Tab 2")

  }
}

