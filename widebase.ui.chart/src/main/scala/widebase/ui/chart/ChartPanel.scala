package widebase.ui.chart

import morechart.chart. { ShiftableChartPanel, ZoomableChartPanel }

import org.jfree.chart.JFreeChart

import scala.swing. { Component, Publisher }

/** Panel of chart.
 * 
 * @param chart of panel
 *
 * @author myst3r10n
 */
class ChartPanel(chart: JFreeChart) extends Component with Publisher {

  /** The [[org.jfree.chart.ChartPanel]] object. */
  override lazy val peer =
    new org.jfree.chart.ChartPanel(chart)
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
    }
}

