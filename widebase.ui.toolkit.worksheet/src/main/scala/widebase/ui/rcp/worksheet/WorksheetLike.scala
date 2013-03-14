package widebase.ui.toolkit.worksheet

import java.awt.event. { ItemEvent, ItemListener }
import java.util.prefs.Preferences

import moreswing.swing. { PopupMenu, TabbedDesktopPane }
import moreswing.swing.i18n. { LAction, LCheckMenuItem, LMenu }

import scala.swing. {

  Action,
  Alignment,
  CheckMenuItem,
  Menu,
  MenuItem,
  Separator,
  TabbedPane

}

import scala.swing.event. { ButtonClicked, MouseMoved }

/** Managed worksheet pages.
 * 
 * @author myst3r10n
 */
class WorksheetLike extends TabbedDesktopPane {

  import TabbedPane.Layout

  private val prefs = Preferences.userNodeForPackage(getClass)

  object Detach extends MenuItem("") {

    action = new Action("Detach") with LAction {

      def apply = { pages.detach(mouseOverTab) }

    }
  }

  object Arrange extends Menu("Arrange") with LMenu {

    object Tile extends MenuItem("") {

      action = new Action("Tile") with LAction {

        def apply = { pages.tile }

      }
    }

    object Horizontal extends MenuItem("") {

      action = new Action("Horizontal") with LAction {

        def apply = { pages.horizontal }

      }
    }

    object Vertical extends MenuItem("") {

      action = new Action("Vertical") with LAction {
        def apply = { pages.vertical }
      }
    }

    contents += Tile += Horizontal += Vertical

  }

  object Behavior extends Menu("Behavior") with LMenu {

    val itemListener = new ItemListener {

      def itemStateChanged(event: ItemEvent) {

        if(event.getStateChange == ItemEvent.SELECTED) {

          contents.clear

          object Grid extends CheckMenuItem("Grid") with LCheckMenuItem {

            selected = gridableDesktop

            reactions += { case ButtonClicked(_) =>

              gridableDesktop = selected

            }
          }

          object Flotable extends  CheckMenuItem("Flotable") with LCheckMenuItem {

            selected = flotableShift

            reactions += { case ButtonClicked(_) =>

              flotableShift = selected
              prefs.putBoolean("worksheet.flotableShift", flotableShift)

            }
          }

          object ScrollLayout extends CheckMenuItem("Scroll Layout") with LCheckMenuItem {

            selected = tabLayoutPolicy == scala.swing.TabbedPane.Layout.Scroll

            reactions += { case ButtonClicked(_) =>

              tabLayoutPolicy =
                if(selected)
                  scala.swing.TabbedPane.Layout.Scroll
                else
                  scala.swing.TabbedPane.Layout.Wrap

              prefs.putInt("worksheet.tabLayoutPolicy", tabLayoutPolicy.id)

            }
          }

          contents += Grid += Flotable += ScrollLayout

        }
      }
    }

    peer.addItemListener(itemListener)

  }

  object Orientation extends Menu("Orientation") with LMenu {

    object Top extends MenuItem("") {

      action = new scala.swing.Action("Top") with LAction {

        def apply = {

          tabPlacement = Alignment.Top
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)

        }
      }
    }

    object Left extends MenuItem("") {

      action = new scala.swing.Action("Left") with LAction {

        def apply = {

          tabPlacement = Alignment.Left
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)

        }
      }
    }

    object Right extends MenuItem("") {

      action = new scala.swing.Action("Right") with LAction {

        def apply = {

          tabPlacement = Alignment.Right
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)

        }
      }
    }

    object Bottom extends MenuItem("") {

      action = new scala.swing.Action("Bottom") with LAction {

        def apply = {

          tabPlacement = Alignment.Bottom
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)

        }
      }
    }

    contents += Top += Left += Right += Bottom

  }

  object Close extends MenuItem("") {

    action = new Action("Close") with LAction {

      def apply = { pages.remove(mouseOverTab) }

    }
  }

  object InactiveOnly extends MenuItem("") {

    action = new Action("Inactive_Only") with LAction {

      def apply = { pages.removeInactive }
    }
  }

  popupMenu = new PopupMenu {

    contents +=
      new Separator +=
      Detach +=
      Arrange +=
      Behavior +=
      Orientation +=
      new Separator +=
      Close +=
      InactiveOnly

  }

  protected var mouseOverTab = -1

  listenTo(mouse.clicks, mouse.moves, selection)

  reactions += { case MouseMoved(_, point, _) =>

    if(!popupMenu.visible)
      mouseOverTab = peer.indexAtLocation(point.x, point.y)

  }

  def restore {

    // Load from config.
    flotableShift = prefs.getBoolean("worksheet.flotableShift", true)
    tabLayoutPolicy = Layout(prefs.getInt("worksheet.tabLayoutPolicy", Layout.Scroll.id))
    tabPlacement = Alignment(prefs.getInt("worksheet.tabPlacement", Alignment.Top.id))

  }
}

