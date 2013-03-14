package widebase.ui.ide

import worksheet.EditPanel

import java.awt.Toolkit
import java.io.File

import scala.swing.Dimension

import widebase.ui.toolkit.AppLike

/** Application.
 * 
 * @author myst3r10n
 */
object App extends AppLike {

  def startup {

    val frame = new AppFrame {

      iconImage = Toolkit.getDefaultToolkit.getImage(
        getClass.getResource("/icon/widebase-16x16.png"))

      override def closeOperation {

        shutdown
        super.closeOperation

      }

      size = new Dimension(0, 0)
      pack
      splitPane.setDividerLocation(splitPane.getHeight - 125)
      visible = true

    }

    EditPanel.intpCfg.out = Some(frame.logPane.writer)
    EditPanel.actor.start

    val init = new File(System.getProperty("user.dir") + "/" + "sbin/Init.scala")

    if(init.exists)
      EditPanel.actor ! Some(EditPanel.load(init))

  }

  def shutdown {

    EditPanel.actor ! EditPanel.Abort

  }
}

