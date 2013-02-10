package widebase

import plot.ui.PlotFrame

import java.awt.Color
import java.awt.geom.Point2D

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
import org.jfree.data.xy. { AbstractIntervalXYDataset, XYSeriesCollection }
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

import widebase.plot.data.time. {

  DateSeries,
  DateSeriesParted,
  DateTimeSeries,
  DateTimeSeriesParted,
  MonthSeries,
  MonthSeriesParted,
  TimestampSeries,
  TimestampSeriesParted

}

import widebase.plot.data.xy.XYSeries

/** Plot package.
 *
 * @author myst3r10n
 */
package object plot {

  protected val figures = HashMap[Int, PlotFrame]()

  /** Current plot. */
  var figure = 1

  /** Set anti alias of plotter.
   *
   * @param flag true or false
   */
  def aa(flag: Boolean) {

    if(figures.contains(figure))
      figures(figure).panel.getChart.setTextAntiAlias(flag)

  }

  /** Close all figures. */
  def clear {

    figures.values.foreach(_.close)
    figures.clear

  }

  /** Anti aliais of text.
   *
   * @param flag true or false
   */
  def taa(flag: Boolean) {

    if(figures.contains(figure))
      figures(figure).panel.getChart.setAntiAlias(flag)

  }

  /** Plot collection of time series.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def time(values: Any*) = {

    var i = 0

    val collection = new TimeSeriesCollection
    val domainAxis = new DateAxis
    val rangeAxis = new NumberAxis
    val renderer = new XYLineAndShapeRenderer(true, false)

    var fromRecord = ArrayBuffer[Int]()
    var tillRecord = ArrayBuffer[Int]()

    while(i < values.length) {

      fromRecord += -1
      tillRecord += -1

      val series =
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
        else
          new TimestampSeriesParted(
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]],
            "")

      i += 2

      breakable {

        while(i < values.length)
          if(i < values.length && values(i).isInstanceOf[String]) {

            if(i + 1 < values.length &&
              !values(i + 1).isInstanceOf[TypedColumn[_]] &&
              !values(i + 1).isInstanceOf[Array[TypedColumn[_]]]) {

              val property = values(i).asInstanceOf[String]

              i += 1

              property match {

                case "from" => fromRecord(collection.getSeriesCount) = values(i).asInstanceOf[Int]
                case "till" => tillRecord(collection.getSeriesCount) = values(i).asInstanceOf[Int]
                case _ =>

              }

              this.property(series, domainAxis, property, values(i))

              i += 1

            } else {

              format(collection, series, renderer, values(i).asInstanceOf[String])

              i += 1

            }
          } else
            break

      }

      if(fromRecord.last == -1 && tillRecord.last != -1) {

        fromRecord(collection.getSeriesCount) = 0

        domainAxis.setLowerBound(series.getTimePeriod(
          fromRecord(collection.getSeriesCount)).getStart.getTime)

      }

      if(fromRecord.last != -1 && tillRecord.last == -1) {

        tillRecord(collection.getSeriesCount) = series.getItemCount - 1

        domainAxis.setUpperBound(series.getTimePeriod(
          tillRecord(collection.getSeriesCount)).getStart.getTime)

      }

      if(series.getKey == "")
        renderer.setSeriesVisibleInLegend(collection.getSeriesCount, false)

      collection.addSeries(series)

    }

    var maxY = Double.MinValue
    var minY = Double.MaxValue

    for(i <- 0 to collection.getSeriesCount - 1) {

      val series = collection.getSeries(i)

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

    val plot = new XYPlot(collection, domainAxis, rangeAxis, renderer)

    val chart = this.chart(plot)

    val plotPanel = new ChartPanel(chart)
      with Publisher
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      shiftable += chart.getXYPlot -> new Point2D.Double(1000.0, 0.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
    }

    show(plotPanel)

    plot

  }

  /** Set title of plotter. */
  def title(title: String) {

    if(figures.contains(figure))
      figures(figure).panel.getChart.setTitle(title)

  }

  /** Set label of x axis. */
  def xlabel(label: String) {

    if(figures.contains(figure)) {

      val plot = figures(figure).panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setLabel(label)

    }

  }

  /** Set label of y axis. */
  def ylabel(label: String) {

    if(figures.contains(figure)) {

      val plot = figures(figure).panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setLabel(label)

    }
  }

  /** Set unit of x axis. */
  def xunit(unit: TickUnitSource) {

    if(figures.contains(figure)) {

      val plot = figures(figure).panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getDomainAxis.setStandardTickUnits(unit)

    }
  }

  /** Set unit of y axis. */
  def yunit(unit: TickUnitSource) {

    if(figures.contains(figure)) {

      val plot = figures(figure).panel.getChart.getPlot

      if(plot.isInstanceOf[XYPlot])
        plot.asInstanceOf[XYPlot].getRangeAxis.setStandardTickUnits(unit)

    }
  }

  /** Plot collection of xy series.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def xy(values: Any*) = {

    var i = 0

    val collection = new XYSeriesCollection
    val domainAxis = new NumberAxis
    val rangeAxis = new NumberAxis
    val renderer = new XYLineAndShapeRenderer(true, false)

    while(i < values.length) {

      var fromRecord = -1
      var tillRecord = -1

      val series = new XYSeries(
        values(i).asInstanceOf[TypedColumn[Number]],
        values(i + 1).asInstanceOf[TypedColumn[Number]],
        "")

      i += 2

      breakable {

        while(i < values.length)
          if(i < values.length && values(i).isInstanceOf[String]) {

            if(i + 1 < values.length &&
              !values(i + 1).isInstanceOf[TypedColumn[_]] &&
              !values(i + 1).isInstanceOf[Array[TypedColumn[_]]]) {

              val property = values(i).asInstanceOf[String]

              i += 1

              property match {

                case "from" => fromRecord = values(i).asInstanceOf[Int]
                case "till" => tillRecord = values(i).asInstanceOf[Int]
                case _ =>

              }

              this.property(series, domainAxis, property, values(i))

              i += 1

            } else {

              format(collection, series, renderer, values(i)
                .asInstanceOf[String])

              i += 1

            }
          } else
            break

      }

      if(fromRecord == -1 && tillRecord != -1)
        domainAxis.setLowerBound(series.getX(0).doubleValue)

      if(fromRecord != -1 && tillRecord == -1)
        domainAxis.setUpperBound(
          series.getX(series.getItemCount - 1).doubleValue)

      if(series.getKey == "")
        renderer.setSeriesVisibleInLegend(collection.getSeriesCount, false)

      collection.addSeries(series)

    }

    val plot = new XYPlot(collection, domainAxis, rangeAxis, renderer)
    val chart = this.chart(plot)

    val plotPanel = new ChartPanel(chart)
      with Publisher
      with ShiftableChartPanel
      with ZoomableChartPanel {

      setMouseZoomable(false)
      setZoomInFactor(0.9)
      setZoomOutFactor(1.1)

      shiftable += chart.getXYPlot -> new Point2D.Double(1.0, 0.1)

      override def paintComponent(g: java.awt.Graphics) {

        setMaximumDrawWidth(getWidth)
        setMaximumDrawHeight(getHeight)

        super.paintComponent(g)

      }
    }

    show(plotPanel)

    plot

  }

  protected def chart(plot: Plot) = new JFreeChart(plot) {

    setAntiAlias(false)
    setTextAntiAlias(false)

  }

  /** Format plotter.
   *
   * @param collection of plotter
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
          widebase.plot.util.ShapeUtilities.createDot(1.0f))

        case "+" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.plot.util.ShapeUtilities.createRegularCross(6.0f))

        case "*" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.plot.util.ShapeUtilities.createDiagonalCross(
            6.0f,
            widebase.plot.util.ShapeUtilities.createRegularCross(6.0f)))

        case "o" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.plot.util.ShapeUtilities.createCircle(6.0f))

        case "x" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.plot.util.ShapeUtilities.createDiagonalCross(6.0f))

        case "^" => renderer.setSeriesShape(
          collection.getSeriesCount,
          ShapeUtilities.createUpTriangle(6.0f))

      }

      if(renderer.isInstanceOf[XYLineAndShapeRenderer])
        renderer.asInstanceOf[XYLineAndShapeRenderer]
          .setSeriesShapesVisible(collection.getSeriesCount, true)

    }
  }

  /** Property plotter.
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

  /** Show plot.
   *
   * @param panel of plotter
   */
  protected def show(panel: ChartPanel with Publisher) = {

    if(!figures.contains(figure)) {

      val frame = new PlotFrame(panel, figure) {

        reactions += {

          case WindowClosing(source) =>
            val frame = source.asInstanceOf[PlotFrame]
            figures -= frame.number
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

