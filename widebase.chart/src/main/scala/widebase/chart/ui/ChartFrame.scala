package widebase.chart.ui

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

import moreswing.swing.i18n. { LFrame, LocaleManager }

import org.jfree.chart.ChartPanel

import scala.swing. {

  BorderPanel,
  Component,
  Dimension,
  Publisher,
  ScrollPane

}

/** Chart's frame.
 * 
 * @param chartPanel self-explanatory
 * @param figure number
 *
 * @author myst3r10n
 */
case class ChartFrame(
  protected var chartPanel: ChartPanel with Publisher,
  val figure: Int,
  width: Int = 800,
  height: Int = 600) extends LFrame {

  title = LocaleManager.text("chart.title_?", figure)

  preferredSize = new Dimension(width, height)

  set(chartPanel)

  def panel = chartPanel

  /** Replace chart panel.
   *
   * @param panel to replace
   *
   * @return frame
   */
  def set(panel: ChartPanel with Publisher) = {

    this.chartPanel = panel

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

