package widebase.workspace.ide

import javax.swing.JSplitPane

import net.liftweb.common.Logger

import scala.swing. { BorderPanel, Dimension }

import widebase.workspace.FrameLike
import widebase.workspace.event. { LocaleChanged, LookAndFeelChanged }
import widebase.workspace.runtime.AppLike

/** Frame of app.
 * 
 * @author myst3r10n
 */
class Frame extends FrameLike with Logger {

  import widebase.workspace.runtime

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  menuBar = new MenuBar(this)
  toolBar = new ToolBar(this)
  pagedPane = new PagedPane

  val panel = new BorderPanel {

    add(toolBar, BorderPanel.Position.North)
    add(pagedPane, BorderPanel.Position.Center)

  }

  val splitPane = new JSplitPane(
    JSplitPane.VERTICAL_SPLIT,
    panel.peer,
    runtime.logPane.component)

  peer.add(splitPane)

  listenTo(this)

  reactions += {

    case event: LocaleChanged =>
      AppLike.prefs.put("app.locale", event.replaced)

    case event: LookAndFeelChanged =>
      AppLike.prefs.put("app.laf", event.replaced.getClassName)

  }
}

