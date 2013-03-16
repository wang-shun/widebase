package widebase.ui.toolkit

import java.awt.event. { ItemEvent, ItemListener }
import java.util.UUID
import java.util.prefs.Preferences

import moreswing.swing.i18n.LCheckMenuItem

import scala.swing. { Alignment, CheckMenuItem, TabbedPane }
import scala.swing.event.ButtonClicked

/* A view menu with and id/menu map.
 *
 * @author myst3r10n
 */
class ViewMenu(sheet: ViewPane) extends PopupMenu with MenuLike {

  import TabbedPane.Layout

  private val prefs = Preferences.userNodeForPackage(getClass)

  this += "Detach" -> new MenuItem(new Action("Detach") {

    def apply = { sheet.pages.detach(sheet.mouseOverTab) }

  } )

  this += "Arrange" -> new Menu("Arrange")
  this.sub("Arrange") += "Tile" -> new MenuItem(new Action("Tile") {

    def apply = { sheet.pages.tile }

  } )
  this.sub("Arrange") += "Horizontal" -> new MenuItem(new Action("Horizontal") {

    def apply = { sheet.pages.horizontal }

  } )
  this.sub("Arrange") += "Vertical" -> new MenuItem(new Action("Vertical") {

    def apply = { sheet.pages.vertical }

  } )

  this += "Behavior" -> new Menu("Behavior") {

    val itemListener = new ItemListener {

      def itemStateChanged(event: ItemEvent) {

        if(event.getStateChange == ItemEvent.SELECTED) {

          contents.clear

          object Grid extends CheckMenuItem("Grid") with LCheckMenuItem {

            selected = sheet.gridableDesktop

            reactions += { case ButtonClicked(_) =>

              sheet.gridableDesktop = selected

            }
          }

          object Flotable extends  CheckMenuItem("Flotable") with LCheckMenuItem {

            selected = sheet.flotableShift

            reactions += { case ButtonClicked(_) =>

              sheet.flotableShift = selected
              prefs.putBoolean("worksheet.flotableShift", sheet.flotableShift)

            }
          }

          object ScrollLayout extends CheckMenuItem("Scroll Layout") with LCheckMenuItem {

            selected = sheet.tabLayoutPolicy == scala.swing.TabbedPane.Layout.Scroll

            reactions += { case ButtonClicked(_) =>

              sheet.tabLayoutPolicy =
                if(selected)
                  scala.swing.TabbedPane.Layout.Scroll
                else
                  scala.swing.TabbedPane.Layout.Wrap

              prefs.putInt("worksheet.tabLayoutPolicy", sheet.tabLayoutPolicy.id)

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

      sheet.tabPlacement = Alignment.Top
      prefs.putInt("worksheet.tabPlacement", sheet.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Left" -> new MenuItem(new Action("Left") {

    def apply = {

      sheet.tabPlacement = Alignment.Left
      prefs.putInt("worksheet.tabPlacement", sheet.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Right" -> new MenuItem(new Action("Right") {

    def apply = {

      sheet.tabPlacement = Alignment.Right
      prefs.putInt("worksheet.tabPlacement", sheet.tabPlacement.id)

    }
  } )
  this.sub("Orientation") += "Bottom" -> new MenuItem(new Action("Bottom") {

    def apply = {

      sheet.tabPlacement = Alignment.Bottom
      prefs.putInt("worksheet.tabPlacement", sheet.tabPlacement.id)

    }
  } )
}

