package widebase.ui.ide

import javax.swing.JSplitPane

import net.liftweb.common.Logger

import scala.swing. { BorderPanel, Dimension }

import widebase.ui.toolkit.FrameLike
import widebase.ui.toolkit.event. { LocaleChanged, LookAndFeelChanged }
import widebase.ui.toolkit.runtime.AppLike

/** Frame of app.
 * 
 * @author myst3r10n
 */
class Frame extends FrameLike with Logger {

  import widebase.ui.toolkit.runtime

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  menuBar = new MenuBar(this)
  toolBar = new ToolBar(this)
  viewPane = new ViewPane { restore("viewPane") }

  val panel = new scala.swing.BorderPanel {

    add(toolBar, BorderPanel.Position.North)
    add(viewPane, BorderPanel.Position.Center)

  }

  val splitPane = new JSplitPane(
    JSplitPane.VERTICAL_SPLIT,
    panel.peer,
    widebase.ui.toolkit.runtime.logPane.component)

  peer.add(splitPane)

  listenTo(this)

  reactions += {

    case event: LocaleChanged =>
      AppLike.prefs.put("app.locale", event.replaced)

    case event: LookAndFeelChanged =>
      AppLike.prefs.put("app.laf", event.replaced.getClassName)

  }
}

