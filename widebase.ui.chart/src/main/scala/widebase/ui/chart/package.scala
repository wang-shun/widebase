package widebase.ui

import java.awt.geom.Point2D

import org.jfree.chart.JFreeChart
import org.jfree.chart.plot. { FastScatterPlot, Plot }
import org.jfree.chart.renderer.xy. { AbstractXYItemRenderer, HighLowRenderer }
import org.jfree.data.time.TimeSeriesCollection

import widebase.ui.chart.plot. { Highlow, Scatter }

/** Charting.
 *
 * @author myst3r10n
 */
package object chart {

  /** Default entities.
   *
   * @author myst3r10n
   */
  protected object default {

    /** Default chart.
     *
     * @param plot of chart
     *
     * @return chart
     */
    def chart(plot: Plot) = new JFreeChart(plot) {

      setAntiAlias(false)
      setTextAntiAlias(false)

    }
  }

  /** Panel with high, low, open and close chart.
   *
   * @param values of data, properties and format
   *
   * @return panel
   */
  def highlowPanel(values: Any*)(implicit
    renderer: AbstractXYItemRenderer = new HighLowRenderer) =
    new ChartPanel(default.chart(Highlow(values:_*))) {

      // Set zoom factor for time based series
      peer.shiftable += peer.getChart.getXYPlot -> new Point2D.Double(1000.0, 0.1)

    }

  /** Panel with 2-D line plot.
   *
   * @param values of data, properties and format
   *
   * @return panel
   */
  def plotPanel(values: Any*) =
    new ChartPanel(default.chart(widebase.ui.chart.plot.Plot(values:_*))) {

      // Set zoom factor for time based series
      if(peer.getChart.getXYPlot.getDataset.isInstanceOf[TimeSeriesCollection])
        peer.shiftable += peer.getChart.getXYPlot -> new Point2D.Double(1000.0, 0.1)
      // Set zoom factor for xy based series
      else
        peer.shiftable += peer.getChart.getXYPlot -> new Point2D.Double(1.0, 0.1)

    }

  def scatterPanel(values: Any*) =
    new ChartPanel(default.chart(Scatter(values:_*))) {

      // Set zoom factor for time based series
      peer.shiftable +=
        peer.getChart.getPlot.asInstanceOf[FastScatterPlot] ->
        new Point2D.Double(1.0, 0.1)

    }
}

