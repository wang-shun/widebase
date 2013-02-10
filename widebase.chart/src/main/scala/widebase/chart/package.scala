package widebase

import chart.ui.ChartFrame

import java.awt.Color
import java.awt.geom.Point2D
import java.awt.image.BufferedImage

import morechart.chart. { ShiftableChartPanel, ZoomableChartPanel }

import org.jfree.chart. { ChartPanel, JFreeChart }

import org.jfree.chart.axis. {

  DateAxis,
  NumberAxis,
  TickUnitSource,
  ValueAxis

}

import org.jfree.chart.plot. { Plot, XYPlot }

import org.jfree.chart.renderer.xy. {

  AbstractXYItemRenderer,
  XYLineAndShapeRenderer

}

import org.jfree.data.general.Series
import org.jfree.data.time. { TimeSeriesCollection, TimeSeriesWorkaround }

import org.jfree.data.xy. {

  AbstractIntervalXYDataset,
  XYSeriesCollection,
  XYSeriesWorkaround

}

import org.jfree.util.ShapeUtilities

import scala.collection.mutable. { ArrayBuffer, HashMap }
import scala.swing.Publisher
import scala.swing.event.WindowClosing
import scala.util.control.Breaks. { break, breakable }

import widebase.db.column. {

  MonthColumn,
  DateColumn,
  DateTimeColumn,
  TimestampColumn,

  TypedColumn

}

import widebase.chart.data.time. {

  DateSeries,
  DateSeriesParted,
  DateTimeSeries,
  DateTimeSeriesParted,
  MonthSeries,
  MonthSeriesParted,
  TimestampSeries,
  TimestampSeriesParted

}

import widebase.chart.data.xy.XYSeries

/** Chart package.
 *
 * @author myst3r10n
 */
package object chart {

  protected val figures = HashMap[Int, ChartFrame]()

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

  /** Current chart. */
  var figure = 1

  /** Set anti alias of chart.
   *
   * @param flag true or false
   *
   * @return chart frame
   */
  def aa(flag: Boolean) = {

    var frame: ChartFrame = null

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

  /** Plot collection of time or xy series.
   *
   * NOTE: octave> doc help
   *
   * @param values of data, properties and format
   *
   * @return chart frame
   */
  def plot(values: Any*) = show(plotPanel(values:_*))

  /** Plot collection of time or xy series.
   *
   * @param values of data, properties and format
   *
   * @return chart panel
   */
  def plotPanel(values: Any*) = {

    var collection: AbstractIntervalXYDataset = null
    var domainAxis: ValueAxis = null

    val rangeAxis = new NumberAxis
    val renderer = new XYLineAndShapeRenderer(true, false)

    var fromRecord = ArrayBuffer[Int]()
    var tillRecord = ArrayBuffer[Int]()

    var i = 0

    while(i < values.length) {

      fromRecord += -1
      tillRecord += -1

      // Init any time series or if not found a xy series
      val series =
        // Init time series
        if(values(i).isInstanceOf[MonthColumn])
          new MonthSeries(
            values(i).asInstanceOf[MonthColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]],
            "")
        else if(values(i).isInstanceOf[DateColumn])
          new DateSeries(
            values(i).asInstanceOf[DateColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]],
            "")
        else if(values(i).isInstanceOf[DateTimeColumn])
          new DateTimeSeries(
            values(i).asInstanceOf[DateTimeColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]],
            "")
        else if(values(i).isInstanceOf[TimestampColumn])
          new TimestampSeries(
            values(i).asInstanceOf[TimestampColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]],
            "")
        // Init time series (partitioned table)
        else if(values(i).isInstanceOf[Array[MonthColumn]])
          new MonthSeriesParted(
            values(i).asInstanceOf[Array[MonthColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]],
            "")
        else if(values(i).isInstanceOf[Array[DateColumn]])
          new DateSeriesParted(
            values(i).asInstanceOf[Array[DateColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]],
            "")
        else if(values(i).isInstanceOf[Array[DateTimeColumn]])
          new DateTimeSeriesParted(
            values(i).asInstanceOf[Array[DateTimeColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]],
            "")
        else if(values(i).isInstanceOf[Array[TimestampColumn]])
          new TimestampSeriesParted(
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]],
            "")
        // Init a xy series
        else
         new XYSeries(
          values(i).asInstanceOf[TypedColumn[Number]],
          values(i + 1).asInstanceOf[TypedColumn[Number]],
          "")

      // Init series collection
      if(collection == null)
        if(series.isInstanceOf[TimeSeriesWorkaround]) { // A time based

          collection = new TimeSeriesCollection
          domainAxis = new DateAxis

        // A xy based
        } else {

          collection = new XYSeriesCollection
          domainAxis = new NumberAxis

        }

      i += 2

      breakable {

        while(i < values.length)
          if(i < values.length && values(i).isInstanceOf[String]) {

            // Is property pair?
            if(i + 1 < values.length &&
              !values(i + 1).isInstanceOf[TypedColumn[_]] &&
              !values(i + 1).isInstanceOf[Array[TypedColumn[_]]]) {

              val property = values(i).asInstanceOf[String]

              i += 1

              // Resolve native properties
              property match {

                case "from" => fromRecord(collection.getSeriesCount) = values(i)
                  .asInstanceOf[Int]

                case "till" => tillRecord(collection.getSeriesCount) = values(i)
                  .asInstanceOf[Int]

                case _ =>

              }

              // Resolve generic properties
              this.property(series, domainAxis, property, values(i))

              i += 1

            // Or a format string?
            } else {

              // Format chart
              format(
                collection,
                series,
                renderer,
                values(i).asInstanceOf[String])

              i += 1

            }
          } else
            break

      }

      // Disable legend if not title was set
      if(series.getKey == "")
        renderer.setSeriesVisibleInLegend(collection.getSeriesCount, false)

      // Final handling for time based series
      if(series.isInstanceOf[TimeSeriesWorkaround]) {

        // Fix lower bound if only upper bound was set
        if(fromRecord.last == -1 && tillRecord.last != -1) {

          fromRecord(collection.getSeriesCount) = 0

          domainAxis.setLowerBound(
            series.asInstanceOf[TimeSeriesWorkaround].getTimePeriod(
              fromRecord(collection.getSeriesCount)).getStart.getTime)

        }

        // Fix upper bound if only lower bound was set
        if(fromRecord.last != -1 && tillRecord.last == -1) {

          tillRecord(collection.getSeriesCount) = series.getItemCount - 1

          domainAxis.setUpperBound(
            series.asInstanceOf[TimeSeriesWorkaround].getTimePeriod(
              tillRecord(collection.getSeriesCount)).getStart.getTime)

        }

        collection.asInstanceOf[TimeSeriesCollection]
          .addSeries(series.asInstanceOf[TimeSeriesWorkaround])

      // Final handling for xy based series
      } else {

          // Fix lower bound if only upper bound was set
          if(fromRecord.last == -1 && tillRecord.last != -1)
            domainAxis.setLowerBound(series.asInstanceOf[XYSeriesWorkaround]
              .getX(0).doubleValue)

          // Fix upper bound if only lower bound was set
          if(fromRecord.last != -1 && tillRecord.last == -1)
            domainAxis.setUpperBound(series.asInstanceOf[XYSeriesWorkaround]
              .getX(series.getItemCount - 1).doubleValue)

        collection.asInstanceOf[XYSeriesCollection]
          .addSeries(series.asInstanceOf[XYSeriesWorkaround])

      }
    }

    // Fix range axis if is time based series collection
    if(collection.isInstanceOf[TimeSeriesCollection]) {

      var maxY = Double.MinValue
      var minY = Double.MaxValue

      for(i <- 0 to collection.getSeriesCount - 1) {

        val series = collection.asInstanceOf[TimeSeriesCollection].getSeries(i)

        val _tillRecord =
            if(tillRecord(i) == -1 || series.getItemCount < tillRecord(i))
            series.getItemCount - 1
          else
            tillRecord(i)

        val _fromRecord =
        if(fromRecord(i) == -1)
          0
        else
          fromRecord(i)

        for(i <- _fromRecord to _tillRecord) {

          val value = series.getDataItem(i).getValue.doubleValue

          if(maxY < value)
            maxY = value

          if(minY > value)
            minY = value

        }
      }

      rangeAxis.setUpperBound(maxY)
      rangeAxis.setLowerBound(minY)

    }

    // Plot liftoff!
    val plot = new XYPlot(collection, domainAxis, rangeAxis, renderer)

    val chart = default.chart(plot)

    new ChartPanel(chart)
      with Publisher
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      // Set zoom factor for time based series
      if(collection.isInstanceOf[TimeSeriesCollection])
        shiftable += chart.getXYPlot -> new Point2D.Double(1000.0, 0.1)
      // Set zoom factor for xy based series
      else
        shiftable += chart.getXYPlot -> new Point2D.Double(1.0, 0.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
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

    var frame: ChartFrame = null

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

    var frame: ChartFrame = null

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

    var frame: ChartFrame = null

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

    var frame: ChartFrame = null

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

    var frame: ChartFrame = null

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

    var frame: ChartFrame = null

    if(figures.contains(figure)) {

      frame = figures(figure)
      val plot = frame.panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setStandardTickUnits(unit)

    }

    frame

  }

  /** Format chart.
   *
   * @param collection of chart
   * @param series of collection
   * @param renderer of plotter
   * @param format itself
   **/
  protected def format(
    collection: AbstractIntervalXYDataset,
    series: Series,
    renderer: AbstractXYItemRenderer,
    format: String) {

    val title = """;.*;""".r.findAllIn(format).toSeq.lastOption

    if(!title.isEmpty)
      series.setKey(title.get.drop(1).dropRight(1))

    val format2 = """;.*;""".r.replaceAllIn(format, "")

    val color = """[0-6|k|r|g|b|m|c|w]""".r
      .findAllIn(format2).toSeq.lastOption

    if(!color.isEmpty)
      color.get match {

        case "0" | "k" => renderer.setSeriesPaint(collection.getSeriesCount, Color.BLACK)
        case "1" | "r" => renderer.setSeriesPaint(collection.getSeriesCount, Color.RED)
        case "2" | "g" => renderer.setSeriesPaint(collection.getSeriesCount, Color.GREEN)
        case "3" | "b" => renderer.setSeriesPaint(collection.getSeriesCount, Color.BLUE)
        case "4" | "m" => renderer.setSeriesPaint(collection.getSeriesCount, Color.MAGENTA)
        case "5" | "c" => renderer.setSeriesPaint(collection.getSeriesCount, Color.CYAN)
        case "6" | "w" => renderer.setSeriesPaint(collection.getSeriesCount, Color.WHITE)

      }

    val line = """-""".r.findAllIn(format2).toSeq.lastOption

    val style = """[.|+|*|o|x|^]""".r
      .findAllIn(format2).toSeq.lastOption


    if(!style.isEmpty && style.get != "-") {

      if(line.isEmpty && renderer.isInstanceOf[XYLineAndShapeRenderer])
        renderer.asInstanceOf[XYLineAndShapeRenderer]
          .setSeriesLinesVisible(collection.getSeriesCount, false)

      style.get match {

        case "." => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.chart.util.ShapeUtilities.createDot(1.0f))

        case "+" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.chart.util.ShapeUtilities.createRegularCross(6.0f))

        case "*" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.chart.util.ShapeUtilities.createDiagonalCross(
            6.0f,
            widebase.chart.util.ShapeUtilities.createRegularCross(6.0f)))

        case "o" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.chart.util.ShapeUtilities.createCircle(6.0f))

        case "x" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.chart.util.ShapeUtilities.createDiagonalCross(6.0f))

        case "^" => renderer.setSeriesShape(
          collection.getSeriesCount,
          ShapeUtilities.createUpTriangle(6.0f))

      }

      if(renderer.isInstanceOf[XYLineAndShapeRenderer])
        renderer.asInstanceOf[XYLineAndShapeRenderer]
          .setSeriesShapesVisible(collection.getSeriesCount, true)

    }
  }

  /** Property chart.
   *
   * @param series of collection
   * @param domainAxis of plotter
   * @param property name
   * @param value of property
   **/
  protected def property(
    series: Series,
    domainAxis: ValueAxis,
    property: String,
    value: Any) {

    property match {

      case "from" =>

        val fromRecord = value.asInstanceOf[Int]

        if(series.isInstanceOf[XYSeries])
          domainAxis.setLowerBound(
            series.asInstanceOf[XYSeries].getX(fromRecord).doubleValue)
        else if(series.isInstanceOf[TimeSeriesWorkaround])
          domainAxis.setLowerBound(
            series.asInstanceOf[TimeSeriesWorkaround]
              .getTimePeriod(fromRecord).getStart.getTime)

      case "till" =>

        val tillRecord = value.asInstanceOf[Int]

        if(series.isInstanceOf[XYSeries])
          domainAxis.setUpperBound(
            series.asInstanceOf[XYSeries].getX(tillRecord).doubleValue)
        else if(series.isInstanceOf[TimeSeriesWorkaround])
          domainAxis.setUpperBound(series.asInstanceOf[TimeSeriesWorkaround]
            .getTimePeriod(tillRecord).getStart.getTime)

      case _ => throw new Exception("Property not found: " + property)

    }
  }

  /** Show chart.
   *
   * @param panel of chart
   *
   * @return chart frame
   */
  protected def show(panel: ChartPanel with Publisher) = {

    if(!figures.contains(figure)) {

      val frame = new ChartFrame(panel, figure) {

        reactions += {

          case WindowClosing(source) =>
            val frame = source.asInstanceOf[ChartFrame]
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

