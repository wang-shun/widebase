package widebase.ui.ide.worksheet

import de.sciss.scalainterpreter. { CodePane, Interpreter, InterpreterPane, SplitPane, Style }

import java.awt.BorderLayout
import java.awt.event. { InputEvent, ItemEvent, ItemListener, KeyEvent }

import java.util.prefs.Preferences

import javax.swing. { ImageIcon, JPopupMenu, KeyStroke }

import moreswing.swing. { PopupMenu, TabbedDesktopPane }
import moreswing.swing.i18n._

import scala.swing._
import scala.swing.event._

import widebase.ui.ide.event._

/** Managed worksheet pages.
 * 
 * @author myst3r10n
 */
class Worksheet extends TabbedDesktopPane {

  import TabbedPane.Layout

  // Load from config.
  private val prefs = Preferences.userNodeForPackage(getClass)
  flotableShift = prefs.getBoolean("worksheet.flotableShift", true)
  tabLayoutPolicy = Layout(prefs.getInt("worksheet.tabLayoutPolicy", Layout.Scroll.id))
  tabPlacement = Alignment(prefs.getInt("worksheet.tabPlacement", Alignment.Top.id))

  popupMenu = new PopupMenu {

    contents += new Menu("New") with LMenu {

      contents += new MenuItem(new Action("Edit") with LAction {
        def apply = { newEdit }
      } )
    }

    contents += new Separator

    contents += new MenuItem(new Action("Detach") with LAction {
      def apply = { pages.detach(mouseOverTab) }
    } )

    contents += new Menu("Arrange") with LMenu {

      contents += new MenuItem(new Action("Tile") with LAction { def apply = {
        pages.tile }
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
                  prefs.putBoolean("worksheet.flotableShift", flotableShift)

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
                  prefs.putInt("worksheet.tabLayoutPolicy", tabLayoutPolicy.id)
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
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Left") with LAction {
        def apply = {
          tabPlacement = Alignment.Left
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Right") with LAction {
        def apply = {
          tabPlacement = Alignment.Right
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)
        }
      } )
      contents += new MenuItem(new scala.swing.Action("Bottom") with LAction {
        def apply = {
          tabPlacement = Alignment.Bottom
          prefs.putInt("worksheet.tabPlacement", tabPlacement.id)
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

  private var mouseOverTab = -1

  listenTo(mouse.clicks, mouse.moves, selection)

  reactions += {
    case MouseMoved(_, point, _) =>
      if(!popupMenu.visible)
        mouseOverTab = peer.indexAtLocation(point.x, point.y)
  }


  private var editCount = BigInt(0)

  def newEdit {

    if(pages.isEmpty)
      editCount = 1
    else
      editCount += 1

    val edit = new EditPanel

    val page = new TabbedDesktopPane.Page(
      LocaleManager.text("Edit_?", editCount),
      new ImageIcon(getClass.getResource("/icon/text-plain.png")),
      edit)

    listenTo(this, edit)

    reactions += {

      case event: RenameTab => selection.page.title = event.name
      case event: SelectPage =>
        if(event.number < pages.size) {

          selection.index = event.number
          selection.page.content.asInstanceOf[EditPanel].codePane.editor.requestFocus

        }
    }

    pages += page
    edit.codePane.editor.requestFocus

  }
}

