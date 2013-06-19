package widebase.toolbox.core.graphics.impl

import java.awt.Cursor
import java.awt.event. { MouseEvent, MouseListener, MouseMotionListener }

import org.jfree.chart. { ChartPanel, JFreeChart }
import org.jfree.chart.plot. { Plot, XYPlot }

import scala.swing. { Component, Publisher }
import scala.swing.event.MouseEntered

/** Panel of axes.
 * 
 * @param chart of panel
 *
 * @author myst3r10n
 */
class AxesPanel extends Component with Publisher {

  import widebase.toolbox.core.graphics.gcf

  /** The [[org.jfree.chart.ChartPanel]] object. */
  override lazy val peer = new ChartPanel(new JFreeChart(new XYPlot))
    with ShiftableChartPanel
    with ZoomableChartPanel {

    setMinimumDrawWidth(0)
    setMinimumDrawHeight(0)
    setMaximumDrawWidth(Int.MaxValue)
    setMaximumDrawHeight(Int.MaxValue)

    setMouseZoomable(false)
    setZoomInFactor(0.9)
    setZoomOutFactor(1.1)

  }

  var hold = false

  peer.addMouseListener(new MouseListener {

    def mouseEntered(e: MouseEvent) {}

    def mouseClicked(e: MouseEvent) {

      if(gcf.toolBar.zoomIn.selected)
        peer.zoomInBoth(0.0, 0.0)

    }

    def mousePressed(e: MouseEvent) {

      peer.setMouseZoomable(gcf.toolBar.zoomIn.selected)

      if(gcf.toolBar.zoomOut.selected) {

        peer.setMouseZoomable(false)
        peer.zoomOutBoth(0.0, 0.0)

      }
    }

    def mouseExited(e: MouseEvent) {}
    def mouseReleased(e: MouseEvent) {}

  } )

  peer.addMouseMotionListener(new MouseMotionListener {

    def mouseDragged(event: MouseEvent) {}

    def mouseMoved(event: MouseEvent) {

      if(gcf.toolBar.pan.selected)
        peer.setCursor(new Cursor(Cursor.HAND_CURSOR))
      else
        peer.setCursor(new Cursor(Cursor.DEFAULT_CURSOR))

    }
  } )
}

/** Companion of [[widebase.toolbox.core.graphics.impl.AxesPanel]]
 *
 * @author myst3r10n
 */
object AxesPanel {
var i = 0
  /** Default entities.
   *
   * @author myst3r10n
   */
  object default {

    /** Default chart.
     *
     * @param plot of chart
     *
     * @return chart
     */
    def chart(plot: org.jfree.chart.plot.Plot) = new JFreeChart(plot) {

      setAntiAlias(false)
      setTextAntiAlias(false)

    }
  }
}

