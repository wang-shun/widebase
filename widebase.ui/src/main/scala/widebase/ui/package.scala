package widebase

import java.awt.image.BufferedImage

import moreswing.swing.i18n.LocaleManager

import org.jfree.chart.axis.TickUnitSource
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy. { CandlestickRenderer, HighLowRenderer }

import scala.collection.mutable.HashMap
import scala.swing. { Component, Dimension, Point, Publisher }
import scala.swing.event.WindowClosing

import widebase.ui.chart. { ChartFrame, ChartPanel }
import widebase.ui.chart.data. { ValueFunction, ValuePartitionFunction }
import widebase.ui.chart.plot. { Highlow, Plot }
import widebase.ui.table. { TableFrame, TablePanel }

/** User interface.
 *
 * @author myst3r10n
 */
package object ui {

  import widebase.ui.chart
  import widebase.ui.table

  /** Hold all figures global. */
  protected val figures = HashMap[Int, FigureFrame]()

  /** Current figure. */
  var figure = 1

  /** Hold figure. */
  var hold = false

  /** Set anti alias of chart.
   *
   * @param flag true or false
   *
   * @return frame
   */
  def aa(flag: Boolean) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      frame.panel.peer.getChart.setTextAntiAlias(flag)

    }

    frame

  }

  /** Anti aliais of text.
   *
   * @param flag true or false
   *
   * @return frame
   */
  def aat(flag: Boolean) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      frame.panel.peer.getChart.setAntiAlias(flag)

    }

    frame

  }

  /** Show candlestick chart.
   *
   * @param values of data, properties and format
   *
   * @return frame
   */
  def candle(values: Any*) = {

    if(hold &&
      figures.contains(figure) &&
      figures(figure).isInstanceOf[ChartFrame])
      Highlow.add(figures(figure).asInstanceOf[ChartFrame]
        .panel.peer.getChart.getPlot, values:_*)(new CandlestickRenderer)
    else
      showChart(chart.highlowPanel(values:_*)(new CandlestickRenderer))

  }

  /** Close all figures. */
  def clear {

    figures.values.foreach(_.close)
    figures.clear

  }

  /** Wraps a function that is called everytime a new value is plotted.
   *
   * @param function itself
   *
   * @return wrapper of function
   */
  def func(function: (Int) => Number) = ValueFunction(function)

  /** Wraps a function that is called everytime a new value is plotted (partition).
   *
   * @param function itself
   *
   * @return wrapper of function
   */
  def funcp(function: (Int, Int) => Number) = ValuePartitionFunction(function)

  /** Show frame with high, low, open and close chart.
   *
   * @param values of data, properties and format
   *
   * @return frame
   */
  def highlow(values: Any*) = {

    if(hold &&
      figures.contains(figure) &&
      figures(figure).isInstanceOf[ChartFrame])
      Highlow.add(figures(figure).asInstanceOf[ChartFrame]
        .panel.peer.getChart.getPlot, values:_*)(new HighLowRenderer)
    else
      showChart(chart.highlowPanel(values:_*)(new HighLowRenderer))

  }

  /** Show frame with 2-D-line plot.
   *
   * NOTE: Supports time or xy series
   *
   * @param values of data, properties and format
   *
   * @return frame
   */
  def plot(values: Any*) = {

    if(hold &&
      figures.contains(figure) &&
      figures(figure).isInstanceOf[ChartFrame])
      Plot.add(figures(figure)
        .asInstanceOf[ChartFrame].panel.peer.getChart.getPlot, values:_*)
    else
      showChart(chart.plotPanel(values:_*))

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
      image = figures(figure).asInstanceOf[ChartFrame]
        .panel.peer.getChart.createBufferedImage(width, height)

    image

  }

  /** Set title of chart.
   *
   * @param title of chart
   *
   * @return frame
   */
  def title(title: String) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      frame.panel.peer.getChart.setTitle(title)

    }

    frame

  }

  /** Show table sheet.
   *
   * @param values of data, properties and format
   *
   * @return frame
   */
  def uitable(values: Any*) = showTable(table.uitablePanel(values:_*))

  /** Set label of x-axis.
   *
   * @param label of x-axis
   *
   * @return frame
   */
  def xlabel(label: String) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      val plot = frame.panel.peer.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setLabel(label)

    }

    frame

  }

  /** Set label of y-axis.
   *
   * @param label of y-axis
   *
   * @return frame
   */
  def ylabel(label: String) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      val plot = frame.panel.peer.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setLabel(label)

    }

    frame

  }

  /** Set unit of x-axis.
   *
   * @param unit of x-axis
   *
   * @return frame
   */
  def xunit(unit: TickUnitSource) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      val plot = frame.panel.peer.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setStandardTickUnits(unit)

    }

    frame

  }

  /** Set unit of y-axis.
   *
   * @param unit of y-axis
   *
   * @return frame
   */
  def yunit(unit: TickUnitSource) = {

    var frame: ChartFrame with FigureFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      val plot = frame.panel.peer.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setStandardTickUnits(unit)

    }

    frame

  }

  /** Show figure of chart.
   *
   * @param panel0 of chart
   *
   * @return frame
   */
  protected def showChart(panel0: ChartPanel with Publisher) = {

    var frame: ChartFrame with FigureFrame = null

    if(!figures.contains(figure) ||
      (figures.contains(figure) && !figures(figure).isInstanceOf[ChartFrame])) {

      if(figures.contains(figure) && !figures(figure).isInstanceOf[ChartFrame])
        figure += 1

      frame = new ChartFrame(panel0) with FigureFrame {

        protected var _panel: Component = panel0

        val figure = Int.box(widebase.ui.figure).toInt

        title = LocaleManager.text("figure.title_?", figure)

//        location = new Point(0, 0)
        preferredSize = new Dimension(800, 600)

        reactions += {

          case WindowClosing(source) =>
            figures -= source.asInstanceOf[FigureFrame].figure

        }
      }

      frame.pack
      frame.visible = true

      figures += figure -> frame.asInstanceOf[FigureFrame]

    } else {

      frame = figures(figure).asInstanceOf[ChartFrame with FigureFrame]
      frame.panel = panel0

    }

    frame

  }

  /** Show figure of table.
   *
   * @param panel0 of table
   *
   * @return frame
   */
  protected def showTable(panel0: TablePanel) = {

    var frame: TableFrame with FigureFrame = null

    if(!figures.contains(figure) ||
      (figures.contains(figure) && !figures(figure).isInstanceOf[TableFrame])) {

      if(figures.contains(figure) && !figures(figure).isInstanceOf[TableFrame])
        figure += 1

      frame = new TableFrame(panel0) with FigureFrame {

        protected var _panel: Component = panel0

        val figure = Int.box(widebase.ui.figure).toInt

        title = LocaleManager.text("figure.title_?", figure)

//        location = new Point(0, 0)
        preferredSize = new Dimension(800, 600)

        reactions += {

          case WindowClosing(source) =>
            figures -= source.asInstanceOf[FigureFrame].figure

        }
      }

      frame.pack
      frame.visible = true

      figures += figure -> frame.asInstanceOf[FigureFrame]

    } else {

      frame = figures(figure).asInstanceOf[TableFrame with FigureFrame]
      frame.panel = panel0

    }

    frame

  }
}

