package widebase.ui.toolkit.worksheet

import java.util.prefs.Preferences

import javax.swing.JOptionPane

import moreswing.swing.i18n. { LAction, LocaleManager }

import scala.swing. { Action, Alignment, MenuItem }

import scala.swing.event. { ButtonClicked, MouseMoved }

/** Managed worksheets.
 * 
 * @author myst3r10n
 */
class WorkbookLike extends WorksheetLike {

  import scala.swing.TabbedPane.Layout
  import moreswing.swing.TabbedPane.Page

  private val prefs = Preferences.userNodeForPackage(getClass)

  object Rename extends MenuItem("") {

    action = new Action("Rename") with LAction {

      def apply = {

        pages(mouseOverTab).title =
          JOptionPane.showInputDialog(
            null,
            LocaleManager.text("Rename_Tab_?",
            pages(mouseOverTab).title), pages(mouseOverTab).title)

      }
    }
  }

  popupMenu.contents += Rename

  override def restore {

    // Load from config.
    flotableShift = prefs.getBoolean("workbook.flotableShift", true)
    tabLayoutPolicy = Layout(prefs.getInt("workbook.tabLayoutPolicy", Layout.Scroll.id))
    tabPlacement = Alignment(prefs.getInt("workbook.tabPlacement", Alignment.Bottom.id))

  }
}

