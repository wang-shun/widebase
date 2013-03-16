package widebase.ui.toolkit

import java.util.prefs.Preferences

import javax.swing.ImageIcon

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing.Alignment
import scala.swing.event.MouseMoved

/** A common trait to build a view pane.
 * 
 * @author myst3r10n
 */
class ViewPane extends TabbedDesktopPane {

  import scala.swing.TabbedPane.Layout
  import scala.util.control.Breaks. { break, breakable }

  private val prefs = Preferences.userNodeForPackage(getClass)

  override val popupMenu = new ViewMenu(this)

  protected[toolkit] var mouseOverTab = -1

  listenTo(mouse.clicks, mouse.moves, selection)

  reactions += { case MouseMoved(_, point, _) =>

    if(!popupMenu.visible)
      mouseOverTab = peer.indexAtLocation(point.x, point.y)

  }

  def add(implicit
    title: String = "View",
    icon: ImageIcon = new ImageIcon(getClass.getResource("/icon/document-multiple.png")),
    view: ViewPane = new ViewPane) = {

    var found = true
    var viewCount = 0
    var viewTitle = ""

    do {

      found = true
      viewCount += 1
      viewTitle = LocaleManager.text(title + "_?", viewCount)

      breakable {

        pages.foreach { page =>

          if(viewTitle == page.title) {

            found = false
            break

          }
        }
      }
    } while(!found)

    val page = new TabbedDesktopPane.Page(viewTitle, icon, view)

    pages += page

    page

  }

  def restore(prefix: String) {

    // Load from config.
    flotableShift = prefs.getBoolean(prefix + ".flotableShift", true)
    tabLayoutPolicy = Layout(prefs.getInt(prefix + ".tabLayoutPolicy", Layout.Scroll.id))
    tabPlacement = Alignment(prefs.getInt(prefix + ".tabPlacement", Alignment.Bottom.id))

  }
}

