package widebase.ui.ide

import java.awt.Toolkit

import scala.swing.Swing

import widebase.ui.workspace.runtime.AppLike

/** App.
 * 
 * @author myst3r10n
 */
class App extends AppLike {

  import widebase.ui.workspace.runtime

  protected var frame0 = new Frame {

    iconImage = Toolkit.getDefaultToolkit.getImage(
      getClass.getResource("/icon/widebase-16x16.png"))

    pack
    splitPane.setDividerLocation(splitPane.getHeight - 125)

  }

  runtime.app += "ide" -> this

  Swing.onEDT {}

  override def frame = frame0

}

