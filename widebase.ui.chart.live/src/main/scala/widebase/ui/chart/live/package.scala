package widebase.ui.chart

import java.awt.geom.Point2D
import java.awt.image.BufferedImage

import morechart.chart. { ShiftableChartPanel, ZoomableChartPanel }
import moreswing.swing.i18n.LocaleManager

import org.jfree.chart. { ChartPanel, JFreeChart }
import org.jfree.chart.axis.TickUnitSource
import org.jfree.chart.plot. { Plot, XYPlot }

import org.jfree.chart.renderer.xy. {

  AbstractXYItemRenderer,
  CandlestickRenderer,
  HighLowRenderer

}

import org.jfree.data.time.TimeSeriesCollection

import scala.collection.mutable.HashMap
import scala.swing.Publisher
import scala.swing.event.WindowClosing

import widebase.ui.chart.plot.Highlow

/** Interactive charting.
 *
 * @author myst3r10n
 */
package object live {

  /** Hold all figures global. */
  protected val figures = HashMap[Int, FigureFrame]()

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

  /** Current figure. */
  var figure = 1

  /** Set anti alias of chart.
   *
   * @param flag true or false
   *
   * @return chart frame
   */
  def aa(flag: Boolean) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      frame.panel.getChart.setTextAntiAlias(flag)

    }

    frame

  }

  /** Close all figures. */
  def clear {

    figures.values.foreach(_.close)
    figures.clear

  }

  /** Show candlestick figure.
   *
   * @param values of data, properties and format
   *
   * @return figure frame
   */
  def candle(values: Any*) =
    show(highlowPanel(values:_*)(new CandlestickRenderer))

  /** Show highlow figure.
   *
   * @param values of data, properties and format
   *
   * @return figure frame
   */
  def highlow(values: Any*) =
    show(highlowPanel(values:_*)(new HighLowRenderer))

  /** Highlow panel.
   *
   * @param values of data, properties and format
   *
   * @return highlow panel
   */
  def highlowPanel(values: Any*)(implicit
    renderer: AbstractXYItemRenderer = new HighLowRenderer) =
    new ChartPanel(default.chart(Highlow(values:_*)))
      with Publisher
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      // Set zoom factor for time based series
      shiftable += getChart.getXYPlot -> new Point2D.Double(1000.0, 0.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
    }

  /** Show plot figure of time or xy series.
   *
   * @param values of data, properties and format
   *
   * @return figure frame
   */
  def plot(values: Any*) = show(plotPanel(values:_*))

  /** Plot panel of time or xy series.
   *
   * @param values of data, properties and format
   *
   * @return plot panel
   */
  def plotPanel(values: Any*) =
    new ChartPanel(default.chart(widebase.ui.chart.plot.Plot(values:_*)))
      with Publisher
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      // Set zoom factor for time based series
      if(getChart.getXYPlot.getDataset.isInstanceOf[TimeSeriesCollection])
        shiftable += getChart.getXYPlot -> new Point2D.Double(1000.0, 0.1)
      // Set zoom factor for xy based series
      else
        shiftable += getChart.getXYPlot -> new Point2D.Double(1.0, 0.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
    }

  /** Print chart.
   *
   * @param values of data, properties and format
   *
   * @return image
   */
  def print(width: Int, height: Int) = {

    var image: BufferedImage = null

    if(figures.contains(figure))
      image = figures(figure).panel.getChart.createBufferedImage(width, height)

    image

  }

  /** Anti aliais of text.
   *
   * @param flag true or false
   *
   * @return chart frame
   */
  def taa(flag: Boolean) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      frame.panel.getChart.setAntiAlias(flag)

    }

    frame

  }

  /** Set title of chart.
   *
   * @param title of chart
   *
   * @return chart frame
   */
  def title(title: String) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      frame.panel.getChart.setTitle(title)

    }

    frame

  }

  /** Set label of x axis.
   *
   * @param label of x axis
   *
   * @return chart frame
   */
  def xlabel(label: String) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      val plot = frame.panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setLabel(label)

    }

    frame

  }

  /** Set label of y axis.
   *
   * @param label of y axis
   *
   * @return chart frame
   */
  def ylabel(label: String) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      val plot = frame.panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setLabel(label)

    }

    frame

  }

  /** Set unit of x axis.
   *
   * @param unit of x axis
   *
   * @return chart frame
   */
  def xunit(unit: TickUnitSource) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      val plot = frame.panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setStandardTickUnits(unit)

    }

    frame

  }

  /** Set unit of y axis.
   *
   * @param unit of y axis
   *
   * @return chart frame
   */
  def yunit(unit: TickUnitSource) = {

    var frame: FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      val plot = frame.panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setStandardTickUnits(unit)

    }

    frame

  }

  /** Show figure.
   *
   * @param panel of chart
   *
   * @return chart frame
   */
  protected def show(panel: ChartPanel with Publisher) = {

    if(!figures.contains(figure)) {

      val frame = new FigureFrame(panel, 800, 600, figure) {

        title = LocaleManager.text("figure.title_?", figure)

        reactions += {

          case WindowClosing(source) =>
            val frame = source.asInstanceOf[FigureFrame]
            figures -= frame.figure
            source.dispose

        }
      }

      frame.pack
      frame.visible = true

      figures += figure -> frame

    } else
      figures(figure).set(panel)

    figures(figure)

  }
}

