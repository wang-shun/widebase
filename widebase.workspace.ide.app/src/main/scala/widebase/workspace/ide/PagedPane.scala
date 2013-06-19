package widebase.workspace.ide.app

import java.util.UUID

import javax.swing. { ImageIcon, JOptionPane }

import moreswing.swing.i18n.LocaleManager

import scala.swing.Separator

import widebase.ui.swing. { Action, Menu, MenuItem }

/** A central place of views.
 * 
 * @author myst3r10n
 */
class PagedPane extends widebase.workspace.PagedPane {

  popupMenu.prepend(UUID.randomUUID.toString)
  popupMenu.prepend("New" -> new Menu("New"))

  popupMenu.sub("New") += "View" -> new MenuItem(new Action("View") {

    def apply = { add }

    icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))

  } )

  popupMenu += UUID.randomUUID.toString -> new Separator

  popupMenu += "Close" -> new MenuItem(new Action("Close") {

      def apply = { pages.remove(mouseOverTab) }

  } )

  popupMenu += "Inactive_Only" -> new MenuItem(new Action("Inactive_Only") {

      def apply = { pages.removeInactive }

  } )

  popupMenu += "Rename" -> new MenuItem(new Action("Rename") {

    def apply = {

      pages(mouseOverTab).title =
        JOptionPane.showInputDialog(
          null,
          LocaleManager.text("Rename_Tab_?",
          pages(mouseOverTab).title), pages(mouseOverTab).title)

    }
  } )
}

