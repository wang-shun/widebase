package widebase.plot.ui

import event. {

  PlotZoomIn,
  PlotZoomInX,
  PlotZoomInY,
  PlotZoomMouse,
  PlotZoomOut,
  PlotZoomOutX,
  PlotZoomOutY

}

import java.awt.Component

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Publisher, ToggleButton }
import scala.swing.event.ButtonClicked

/** Plot frame's tool bar.
 * 
 * @author myst3r10n
 */
class PlotToolBar(n: String, o: Int) extends JToolBar(n, o) with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(o: Int) = this("", o)
  def this(n: String) = this(n, SwingConstants.HORIZONTAL)

  setFloatable(false)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in.png"))
    tooltip = LocaleManager.text("Zoom_in")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomIn)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out.png"))
    tooltip = LocaleManager.text("Zoom_out")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomOut)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in-x.png"))
    tooltip = LocaleManager.text("Zoom_in_x")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomInX)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out-x.png"))
    tooltip = LocaleManager.text("Zoom_out_x")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomOutX)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-in-y.png"))
    tooltip = LocaleManager.text("Zoom_in_y")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomInY)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-out-y.png"))
    tooltip = LocaleManager.text("Zoom_out_y")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomOutY)

    }
  } ).peer)

  addSeparator

  add((new ToggleButton {

    icon = new ImageIcon(getClass.getResource("/icon/zoom-mouse.png"))
    tooltip = LocaleManager.text("Zoom_mouse")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => PlotToolBar.this.publish(PlotZoomMouse(selected))

    }
  } ).peer)
}

