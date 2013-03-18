package widebase.ui.workspace

import java.awt.event. { ActionEvent, InputEvent, KeyEvent }
import java.util.prefs.Preferences

import javax.swing. { AbstractAction, ImageIcon, JComponent, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing. { Alignment, Component, ScrollPane }
import scala.swing.event.MouseMoved

/** A common trait to build a desktop.
 * 
 * @author myst3r10n
 */
class PagedPane extends TabbedDesktopPane {

  import scala.swing.TabbedPane.Layout
  import scala.util.control.Breaks. { break, breakable }

  override val popupMenu = new PageMenu(this)

  listenTo(mouse.clicks, mouse.moves, selection)

  reactions += { case MouseMoved(_, point, _) =>

    if(!popupMenu.visible)
      mouseOverTab = peer.indexAtLocation(point.x, point.y)

  }

  val prefs = Preferences.userNodeForPackage(getClass)

  def backup = "PagedPane"

  flotableShift = prefs.getBoolean(backup + ".flotableShift", true)
  tabLayoutPolicy = Layout(prefs.getInt(backup + ".tabLayoutPolicy", Layout.Scroll.id))
  tabPlacement = Alignment(prefs.getInt(backup + ".tabPlacement", Alignment.Bottom.id))

  def add(implicit
    title: String = "Page",
    icon: ImageIcon = new ImageIcon(getClass.getResource("/icon/document-multiple.png")),
    content: Component = new PagedPane) = {

    var found = true
    var pageCount = 0
    var newTitle = ""

    do {

      found = true
      pageCount += 1
      newTitle = LocaleManager.text(title + "_?", pageCount)

      breakable {

        pages.foreach { page =>

          if(newTitle == page.title) {

            found = false
            break

          }
        }
      }
    } while(!found)

    val page = new TabbedDesktopPane.Page(newTitle, icon, content)

    pages += page

    page

  }

  protected var _mouseOverTab = -1

  def mouseOverTab = _mouseOverTab

  protected def mouseOverTab_=(index: Int) {

    _mouseOverTab = index

  }
}

