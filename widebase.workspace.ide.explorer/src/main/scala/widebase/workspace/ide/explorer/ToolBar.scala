package widebase.workspace.ide.explorer

import java.io.File

import java.util.UUID

import javax.swing. { ImageIcon, JTree }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Separator }

import widebase.workspace. { Action, Tree }

/** Tool bar of tree.
 *
 * @author myst3r10n
 */
class ToolBar(tree: Tree) extends widebase.workspace.ToolBar {

  import widebase.workspace.runtime

  peer.setFloatable(false)

  this += "Refresh" -> new Button(
    new Action("") {

      icon = new ImageIcon(getClass.getResource("/icon/view-refresh.png"))
      toolTip = LocaleManager.text("Refresh")

      def apply {

        val root = new File(System.getProperty("user.dir") + "/usr") {

          override def toString = getName

        }

        tree.peer.setModel(new FileTreeModel(root))

      }
    }
  )
}

