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

import java.awt.BorderLayout

import moreswing.swing.i18n.LFrame

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
 *
 * @author myst3r10n
 */
class ChartFrame(protected var panel0: ChartPanel = null) extends LFrame {

  val toolBar = new ChartToolBar
  val scrollPane = new ScrollPane

  if(panel0 != null)
    panel = panel0

  def panel = panel0

  def panel_=(panel: ChartPanel) {

    scrollPane.contents = new Component {

      deafTo(panel)
      listenTo(panel)

      override lazy val peer = panel.peer

    }

    panel0 = panel

  }

  contents = new scala.swing.BorderPanel {

    peer.add(toolBar, BorderLayout.NORTH)
    add(scrollPane, BorderPanel.Position.Center)

    listenTo(toolBar)

    reactions += {

      case ChartZoomIn => panel.peer.zoomInBoth(0.0, 0.0)
      case ChartZoomInX => panel.peer.zoomInDomain(0.0, 0.0)
      case ChartZoomInY => panel.peer.zoomInRange(0.0, 0.0)
      case ChartZoomOut => panel.peer.zoomOutBoth(0.0, 0.0)
      case ChartZoomOutX => panel.peer.zoomOutDomain(0.0, 0.0)
      case ChartZoomOutY => panel.peer.zoomOutRange(0.0, 0.0)
      case event: ChartMouseDragShiftable => panel.peer.mouseDragShiftable = event.enabled
      case event: ChartMouseWheelZoomable => panel.peer.mouseWheelZoomable = event.enabled
      case event: ChartMouseZoomable => panel.peer.setMouseZoomable(event.enabled)

    }

  }
}

