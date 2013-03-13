package widebase.ui.ide

import event. { NewEdit, NewWorksheet }

import javax.swing. { ImageIcon, JToolBar, SwingConstants }

import scala.swing. { Button, Publisher }
import scala.swing.event.ButtonClicked

import moreswing.swing.i18n.LocaleManager

/** Main frame's tool bar.
 * 
 * @author myst3r10n
 */
class AppMainToolBar(n: String, o: Int) extends JToolBar(n, o) with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(o: Int) = this("", o)
  def this(n: String) = this(n, SwingConstants.HORIZONTAL)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))
    tooltip = LocaleManager.text("New_Edit")

    listenTo(this)
    reactions += {
      case ButtonClicked(_) =>
        AppMainToolBar.this.publish(NewEdit)
    }

  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/window-new.png"))
    tooltip = LocaleManager.text("New_Worksheet")

    listenTo(this)
    reactions += {
      case ButtonClicked(_) =>
        AppMainToolBar.this.publish(NewWorksheet)
    }
  } ).peer)
}

