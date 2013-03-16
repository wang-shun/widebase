package widebase.ui.ide

import java.io.FileNotFoundException

import net.liftweb.common. { Loggable, Logger }

/** Main.
 * 
 * @author myst3r10n
 */
object Main extends Logger with Loggable {

  import widebase.ui.toolkit

  def main(args: Array[String]) {

    if(false) // Hardcode
      new widebase.ui.ide.App
    else try {

      toolkit.runtime.launch(System.getProperty("user.dir") + "/" + "etc/ide.scala")

    } catch {

      case e: FileNotFoundException => error(e)

    }
  }
}

