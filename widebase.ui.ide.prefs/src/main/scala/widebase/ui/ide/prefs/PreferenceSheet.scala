package widebase.ui.ide.prefs

import java.awt.event. { ItemEvent, ItemListener }

import java.util.prefs.Preferences

import javax.swing.ImageIcon

import moreswing.swing. { PopupMenu, TabbedDesktopPane }
import moreswing.swing.i18n. { LAction, LCheckMenuItem, LMenu, LocaleManager }
import moreswing.swing.plaf.Redesign

import scala.swing. {

  Action,
  Alignment,
  BorderPanel,
  CheckMenuItem,
  Label,
  Menu,
  MenuItem,
  Separator,
  TabbedPane

}

import scala.swing.event. { ButtonClicked, MouseMoved }

/** Managed all preference tabs.
 *
 * Is special sheet inside workbook.
 *
 * @author myst3r10n
 */
class PreferenceSheet extends TabbedDesktopPane with Redesign {

  import TabbedPane.Layout

  // Load from config.
  private val prefs = Preferences.userNodeForPackage(getClass)
  flotableShift = prefs.getBoolean("preferences.flotableShift", true)

  tabLayoutPolicy = Layout(prefs.getInt(
    "preferences.tabLayoutPolicy",
    Layout.Scroll.id))

  tabPlacement = Alignment(prefs.getInt(
    "preferences.tabPlacement",
    Alignment.Top.id))

  popupMenu = new PopupMenu with Redesign {

    contents += new MenuItem(new Action("Detach") with LAction { def apply = {
      pages.detach(mouseOverTab) }
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
                  prefs.putBoolean("preferences.flotableShift", flotableShift)

              }
            }

            contents += new CheckMenuItem("Scroll Layout") with LCheckMenuItem {

              selected = tabLayoutPolicy == scala.swing.TabbedPane.Layout.Scroll
              reactions += {
                case ButtonClicked(_) =>
                  tabLayoutPolicy =
                    if(selected)
                      scala.swing.TabbedPane.Layout.Scroll
                    else
                      scala.swing.TabbedPane.Layout.Wrap
                  prefs.putInt("preferences.tabLayoutPolicy", tabLayoutPolicy.id)
              }
            }
          }
        }
      } )
    }

    contents += new Menu("Orientation") with LMenu {

      contents += new MenuItem(new scala.swing.Action("Top") with LAction {
        def apply = {
          tabPlacement = Alignment.Top
          prefs.putInt("preferences.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Left") with LAction {
        def apply = {
          tabPlacement = Alignment.Left
          prefs.putInt("preferences.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Right") with LAction {
        def apply = {
          tabPlacement = Alignment.Right
          prefs.putInt("preferences.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Bottom") with LAction {
        def apply = {
          tabPlacement = Alignment.Bottom
          prefs.putInt("preferences.tabPlacement", tabPlacement.id)
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
  }

  protected var mouseOverTab = -1
  listenTo(mouse.clicks, mouse.moves, selection)
  reactions += {
    case MouseMoved(_, point, _) =>
      if(!popupMenu.visible)
        mouseOverTab = peer.indexAtLocation(point.x, point.y)
  }

  pages += new TabbedDesktopPane.Page(
    LocaleManager.text("Preference 1"),
    new ImageIcon(getClass.getResource("/icon/configure.png")),
    new Label) {

      border.add(
        new Label(LocaleManager.text("Content_?", 1)),
        BorderPanel.Position.Center)

  }

  pages += new TabbedDesktopPane.Page(
    LocaleManager.text("Preference 2"),
    new ImageIcon(getClass.getResource("/icon/configure.png")),
    new Label) {

      border.add(
        new Label(LocaleManager.text("Content_?", 2)),
        BorderPanel.Position.Center)

  }

  if(pages.length > 0)
    selection.index = 0

}

