package widebase.workspace.ide

import java.io.FileNotFoundException

import javax.swing.ImageIcon

import net.liftweb.common. { Loggable, Logger }

import scala.swing.Swing

/** Main.
 * 
 * @author myst3r10n
 */
object Main extends Logger with Loggable {

  import widebase.workspace.runtime

  def main(args: Array[String]) {

    if(false) // Hardcode
      new App
    else
      runtime.launch(
        System.getProperty("user.dir") + "/" + "etc/ide.scala",
        new ImageIcon(getClass.getResource("/icon/widebase-logo-128x128.png")))

  }
}

