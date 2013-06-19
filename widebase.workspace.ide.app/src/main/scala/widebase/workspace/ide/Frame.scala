package widebase.workspace.ide.app

import java.util.prefs.Preferences

import javax.swing.JSplitPane

import net.liftweb.common.Logger

import scala.swing. {

  BorderPanel,
  Component,
  Dimension,
  Orientation,
  SplitPane

}

import widebase.workspace.FrameLike
import widebase.workspace.event. { LocaleChanged, LookAndFeelChanged }

/** Frame of app.
 * 
 * @author myst3r10n
 */
class Frame extends FrameLike with Logger {

  import widebase.workspace.runtime

  val prefs = Preferences.userRoot

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  menuBar = new MenuBar(this)
  toolBar = new ToolBar(this)
  pagedPane = new PagedPane

  val splitPane = new SplitPane(
    Orientation.Horizontal,
    pagedPane,
    new Component {

      override lazy val peer = runtime.logPane.component

    }
  )

  panel = new BorderPanel {

    add(toolBar, BorderPanel.Position.North)
    add(splitPane, BorderPanel.Position.Center)

  }

  contents = panel

  listenTo(this)

  reactions += {

    case scala.swing.event.WindowClosed(_) => runtime.shutdown
    case event: LocaleChanged => prefs.put("app.locale", event.replaced)

    case event: LookAndFeelChanged =>
      prefs.put("app.laf", event.replaced.getClassName)

  }

  override def closeOperation() {

    dispose

  }
}

