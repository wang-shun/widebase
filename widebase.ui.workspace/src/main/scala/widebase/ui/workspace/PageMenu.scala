package widebase.ui.workspace

import java.awt.event. { ItemEvent, ItemListener }
import java.util.UUID

import moreswing.swing.i18n.LCheckMenuItem

import scala.swing. { Alignment, CheckMenuItem, TabbedPane }
import scala.swing.event.ButtonClicked

/* A page menu with and id/menu map.
 *
 * @author myst3r10n
 */
class PageMenu(pane: PagedPane) extends PopupMenu with MenuLike {

  import TabbedPane.Layout

  this += "Detach" -> new MenuItem(new Action("Detach") {

    def apply = { pane.pages.detach(pane.mouseOverTab) }

  } )

  this += "Arrange" -> new Menu("Arrange")
  this.sub("Arrange") += "Tile" -> new MenuItem(new Action("Tile") {

    def apply = { pane.pages.tile }

  } )
  this.sub("Arrange") += "Horizontal" -> new MenuItem(new Action("Horizontal") {

    def apply = { pane.pages.horizontal }

  } )
  this.sub("Arrange") += "Vertical" -> new MenuItem(new Action("Vertical") {

    def apply = { pane.pages.vertical }

  } )

  this += "Behavior" -> new Menu("Behavior") {

    val itemListener = new ItemListener {

      def itemStateChanged(event: ItemEvent) {

        if(event.getStateChange == ItemEvent.SELECTED) {

          contents.clear

          object Grid extends CheckMenuItem("Grid") with LCheckMenuItem {

            selected = pane.gridableDesktop

            reactions += { case ButtonClicked(_) =>

              pane.gridableDesktop = selected

            }
          }

          object Flotable extends  CheckMenuItem("Flotable") with LCheckMenuItem {

            selected = pane.flotableShift

            reactions += { case ButtonClicked(_) =>

              pane.flotableShift = selected
              pane.prefs.putBoolean(pane.backup + ".flotableShift", pane.flotableShift)

            }
          }

          object ScrollLayout extends CheckMenuItem("Scroll Layout") with LCheckMenuItem {

            selected = pane.tabLayoutPolicy == scala.swing.TabbedPane.Layout.Scroll

            reactions += { case ButtonClicked(_) =>

              pane.tabLayoutPolicy =
                if(selected)
                  scala.swing.TabbedPane.Layout.Scroll
                else
                  scala.swing.TabbedPane.Layout.Wrap

              pane.prefs.putInt(pane.backup + ".tabLayoutPolicy", pane.tabLayoutPolicy.id)

            }
          }

          contents += Grid += Flotable += ScrollLayout

        }
      }
    }

    peer.addItemListener(itemListener)

  }

  this += "Orientation" -> new Menu("Orientation")
  this.sub("Orientation") += "Top" -> new MenuItem(new Action("Top") {

    def apply = {

      pane.tabPlacement = Alignment.Top
      pane.prefs.putInt(pane.backup + ".tabPlacement", pane.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Left" -> new MenuItem(new Action("Left") {

    def apply = {

      pane.tabPlacement = Alignment.Left
      pane.prefs.putInt(pane.backup + ".tabPlacement", pane.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Right" -> new MenuItem(new Action("Right") {

    def apply = {

      pane.tabPlacement = Alignment.Right
      pane.prefs.putInt(pane.backup + ".tabPlacement", pane.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Bottom" -> new MenuItem(new Action("Bottom") {

    def apply = {

      pane.tabPlacement = Alignment.Bottom
      pane.prefs.putInt(pane.backup + ".tabPlacement", pane.tabPlacement.id)

    }
  } )
}

