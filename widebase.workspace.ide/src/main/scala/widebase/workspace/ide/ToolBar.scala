package widebase.workspace.ide

import javax.swing.ImageIcon

import scala.swing.Button

import widebase.workspace.Action

/** Tool bar of app frame.
 * 
 * @author myst3r10n
 */
class ToolBar(frame: Frame) extends widebase.workspace.ToolBar {

  this += "Edit" -> new Button(
    new Action("") {

      def apply {

        frame.pagedPane.add

      }

      icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))

    }
  )
}

