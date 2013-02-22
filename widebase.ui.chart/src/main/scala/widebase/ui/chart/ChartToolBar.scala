package widebase.ui.chart

import event. {

  ChartMouseDragShiftable,
  ChartMouseWheelZoomable,
  ChartMouseZoomable,
  ChartZoomIn,
  ChartZoomInX,
  ChartZoomInY,
  ChartZoomOut,
  ChartZoomOutX,
  ChartZoomOutY

}

import java.awt.Component

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Publisher, ToggleButton }
import scala.swing.event.ButtonClicked

/** Tool bar of frame.
 *
 * @param name of tool bar
 * @param orientation of tool bar
 *
 * @author myst3r10n
 */
class ChartToolBar(
  name: String,
  orientation: Int)
  extends JToolBar(name, orientation) with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(orientation: Int) = this("", orientation)
  def this(name: String) = this(name, SwingConstants.HORIZONTAL)

  setFloatable(false)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in.png"))
    tooltip = LocaleManager.text("Chart_zoom_in")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomIn)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out.png"))
    tooltip = LocaleManager.text("Chart_zoom_out")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomOut)

    }
  } ).peer)

  add((new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/transform-move.png"))
    tooltip = LocaleManager.text("Chart_mouse_drag_shiftable")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartMouseDragShiftable(selected))

    }
  } ).peer)

  addSeparator

  add((new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-mouse.png"))
    tooltip = LocaleManager.text("Chart_mouse_zoomable")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartMouseZoomable(selected))

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in-x.png"))
    tooltip = LocaleManager.text("Chart_zoom_in_x")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomInX)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out-x.png"))
    tooltip = LocaleManager.text("Chart_zoom_out_x")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomOutX)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in-y.png"))
    tooltip = LocaleManager.text("Chart_zoom_in_y")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomInY)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out-y.png"))
    tooltip = LocaleManager.text("Chart_zoom_out_y")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => ChartToolBar.this.publish(ChartZoomOutY)

    }
  } ).peer)
}

