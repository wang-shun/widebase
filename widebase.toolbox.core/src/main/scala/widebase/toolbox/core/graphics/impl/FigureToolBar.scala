package widebase.toolbox.core.graphics.impl

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Component, Publisher, ToggleButton }
import scala.swing.event.ButtonClicked

/** Tool bar of figure.
 *
 * @param name of tool bar
 * @param orientation of tool bar
 *
 * @author myst3r10n
 */
class FigureToolBar(
  name: String,
  orientation: Int)
  extends Component with Publisher {

  /** The [[javax.swing.JToolBar]] object. */
  override lazy val peer = new JToolBar(name, orientation)

  val pan: ToggleButton = new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/transform-move.png"))
    tooltip = LocaleManager.text("Chart_mouse_drag_shiftable")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) =>

        zoomIn.selected = false
        zoomOut.selected = false

    }
  }

  val zoomIn: ToggleButton = new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in.png"))
    tooltip = LocaleManager.text("Chart_mouse_zoom_in")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) =>

        pan.selected = false
        zoomOut.selected = false

    }
  }

  val zoomOut: ToggleButton = new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out.png"))
    tooltip = LocaleManager.text("Chart_mouse_zoom_out")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) =>

        pan.selected = false
        zoomIn.selected = false

    }
  }

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(orientation: Int) = this("", orientation)
  def this(name: String) = this(name, SwingConstants.HORIZONTAL)

  peer.setFloatable(false)

  peer.add(zoomIn.peer)
  peer.add(zoomOut.peer)
  peer.add(pan.peer)

}

