package widebase.ui.chart

import event. {

  ChartZoomIn,
  ChartZoomInX,
  ChartZoomInY,
  ChartZoomMouse,
  ChartZoomOut,
  ChartZoomOutX,
  ChartZoomOutY

}

import java.awt.BorderLayout

import moreswing.swing.i18n.LFrame

import org.jfree.chart.ChartPanel

import scala.swing. {

  BorderPanel,
  Component,
  Dimension,
  Publisher,
  ScrollPane

}

/** Frame of chart.
 * 
 * @param panel0 of chart
 * @param width of frame
 * @param height of frame
 *
 * @author myst3r10n
 */
class ChartFrame(
  protected var panel0: ChartPanel with Publisher,
  width: Int = 800,
  height: Int = 600)
  extends LFrame {

  preferredSize = new Dimension(width, height)

  set(panel0)

  def panel = panel0

  /** Replace chart panel.
   *
   * @param panel to replace
   *
   * @return frame
   */
  def set(panel: ChartPanel with Publisher) = {

    this.panel0 = panel

    val toolBar = new ChartToolBar

    val scrollPane = new ScrollPane {

      contents = new Component {

        ChartFrame.this.deafTo(ChartFrame.this.panel)
        ChartFrame.this.listenTo(panel)

        override lazy val peer = panel

      }
    }

    contents = new scala.swing.BorderPanel {

      peer.add(toolBar, BorderLayout.NORTH)
      add(scrollPane, BorderPanel.Position.Center)

    }

    listenTo(this, toolBar)

    reactions += {

      case ChartZoomIn => panel.zoomInBoth(0.0, 0.0)
      case ChartZoomInX => panel.zoomInDomain(0.0, 0.0)
      case ChartZoomInY => panel.zoomInRange(0.0, 0.0)
      case event: ChartZoomMouse => panel.setMouseZoomable(event.enabled)
      case ChartZoomOut => panel.zoomOutBoth(0.0, 0.0)
      case ChartZoomOutX => panel.zoomOutDomain(0.0, 0.0)
      case ChartZoomOutY => panel.zoomOutRange(0.0, 0.0)

    }

    this

  }
}

