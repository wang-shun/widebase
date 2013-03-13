package widebase.ui.ide.worksheet

import java.awt.BorderLayout
import java.awt.event. { ItemEvent, ItemListener }
import java.util.prefs.Preferences

import javax.swing. { ImageIcon, JOptionPane }

import moreswing.swing. { PopupMenu, TabbedDesktopPane }
import moreswing.swing.i18n. { LAction, LCheckMenuItem, LMenu, LocaleManager }

import scala.swing. {

  Action,
  Alignment,
  CheckMenuItem,
  Menu,
  MenuItem,
  Separator

}

import scala.swing.event. { ButtonClicked, MouseMoved }

import widebase.ui.ide.event. { NewEdit, NewWorksheet }

/** Managed worksheets.
 * 
 * @author myst3r10n
 */
class Workbook extends TabbedDesktopPane {

  import scala.swing.TabbedPane.Layout
  import moreswing.swing.TabbedPane.Page

  // Load from config.
  private val prefs = Preferences.userNodeForPackage(getClass)
  flotableShift = prefs.getBoolean("workbook.flotableShift", true)
  tabLayoutPolicy = Layout(prefs.getInt("workbook.tabLayoutPolicy", Layout.Scroll.id))
  tabPlacement = Alignment(prefs.getInt("workbook.tabPlacement", Alignment.Bottom.id))

  popupMenu = new PopupMenu {

    contents += new Menu("New") with LMenu {
      contents += new MenuItem(new Action("Edit") with LAction {
        tooltip = LocaleManager.text("New_Edit")
        def apply = { Workbook.this.publish(NewEdit) }
      } ) { icon = new ImageIcon(getClass.getResource("/icon/tab-new.png")) }

      contents += new MenuItem(new Action("Worksheet") with LAction {

        tooltip = "New_Worksheet"
        def apply = { Workbook.this.publish(NewWorksheet) }

      } ) { icon = new ImageIcon(getClass.getResource("/icon/window-new.png")) }
    }

    contents += new Separator

    contents += new MenuItem(new Action("Detach") with LAction {
      def apply = { pages.detach(mouseOverTab) }
    } )

    contents += new Menu("Arrange") with LMenu {

      contents += new MenuItem(new Action("Tile") with LAction {
        def apply = { pages.tile }
      } )
      contents += new MenuItem(new Action("Horizontal") with LAction {
        def apply = { pages.horizontal }
      } )
      contents += new MenuItem(new Action("Vertical") with LAction {
        def apply = { pages.vertical }
      } )
    }

    contents += new Menu("Behavior") with LMenu {
      peer.addItemListener(new ItemListener {
        def itemStateChanged(event: ItemEvent) {
          if(event.getStateChange == ItemEvent.SELECTED) {

            contents.clear
            contents += new CheckMenuItem("Grid") with LCheckMenuItem {

              selected = gridableDesktop
              reactions += {
                case ButtonClicked(_) =>
                  gridableDesktop = selected
              }
            }

            contents += new CheckMenuItem("Flotable") with LCheckMenuItem {

              selected = flotableShift
              reactions += {

                case ButtonClicked(_) =>
                  flotableShift = selected
                  prefs.putBoolean("workbook.flotableShift", flotableShift)

              }
            }

            contents += new CheckMenuItem("Scroll_Layout") with LCheckMenuItem {

              selected = tabLayoutPolicy == scala.swing.TabbedPane.Layout.Scroll
              reactions += {
                case ButtonClicked(_) =>
                  tabLayoutPolicy =
                    if(selected)
                      scala.swing.TabbedPane.Layout.Scroll
                    else
                      scala.swing.TabbedPane.Layout.Wrap
                  prefs.putInt("workbook.tabLayoutPolicy", tabLayoutPolicy.id)
              }
            }
          }
        }
      } )
    }

    contents += new Menu("Orientation") with LMenu {

      contents += new MenuItem(new Action("Top") with LAction {
        def apply = {
          tabPlacement = Alignment.Top
          prefs.putInt("workbook.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new Action("Left") with LAction {
        def apply = {
          tabPlacement = Alignment.Left
          prefs.putInt("workbook.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new Action("Right") with LAction {
        def apply = {
          tabPlacement = Alignment.Right
          prefs.putInt("workbook.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new Action("Bottom") with LAction {
        def apply = {
          tabPlacement = Alignment.Bottom
          prefs.putInt("workbook.tabPlacement", tabPlacement.id)
        }
      } )
    }

    contents += new Separator

    contents += new MenuItem(new Action("Close") with LAction {
      def apply = { pages.remove(mouseOverTab) }
    } )
    contents += new MenuItem(new Action("Inactive_Only") with LAction {
      def apply = { pages.removeInactive }
    } )

    contents += new Separator

    contents += new MenuItem(new Action("Rename") with LAction {
      def apply = {

        pages(mouseOverTab).title =
          JOptionPane.showInputDialog(
            null,
            LocaleManager.text("Rename_Tab_?",
            pages(mouseOverTab).title), pages(mouseOverTab).title)

      }
    } )
  }

  private var mouseOverTab = -1
  listenTo(mouse.clicks, mouse.moves, selection)
  reactions += {

      case MouseMoved(_, point, _) => 

        if(!popupMenu.visible)
          mouseOverTab = peer.indexAtLocation(point.x, point.y)

  }

  private var worksheetCount = BigInt(0)

  def newWorksheet {

    if(pages.isEmpty)
      worksheetCount = 1
    else
      worksheetCount += 1

    pages += new TabbedDesktopPane.Page(
      LocaleManager.text("Worksheet_?", worksheetCount),
      new ImageIcon(getClass.getResource("/icon/document-multiple.png")),
      new Worksheet)

  }
}

