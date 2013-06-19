package widebase.toolbox.core

import java.awt.Color
import java.util.UUID

import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot

import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.util.ShapeUtilities

import widebase.db.column. {

  MonthColumn,
  DateColumn,
  DateTimeColumn,
  DoubleColumn,
  TimestampColumn,

  TypedColumn

}

import widebase.ui.chart.data. { ValueFunction, ValuePartitionFunction }

import widebase.ui.chart.data.time. {

  DatePartitionSeries,
  DateSeries,
  DateTimePartitionSeries,
  DateTimeSeries,
  MonthPartitionSeries,
  MonthSeries,
  TimestampPartitionSeries,
  TimestampSeries

}

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Timeseries.
 *
 * @author myst3r10n
 */
package object timeseries {

  import widebase.toolbox.core.graphics. { gca, gcf }

  /** Timeseries.
   *
   * @param data of time series
   * @param time of time series
   *
   * @author myst3r10n
   */
  case class timeseries(val data: Any, val time: Any)

  /** Plot time series into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param ts time series
   * @param values of linespec or properties
   *
   * @return plot handle
   */
  def plot(ts: timeseries, values: Any*): XYPlot = plot(gca, ts, values:_*)

  /** Plot time series into specific axes.
   *
   * @param axes handle
   * @param ts time series
   * @param values of linespec or properties
   *
   * @return plot handle
   */
  def plot(axes: AxesPanel, ts: timeseries, values: Any*) = {

    if(!axes.peer.getChart.getPlot.isInstanceOf[XYPlot])
      axes.peer.setChart(AxesPanel.default.chart(new XYPlot))

    val plot = axes.peer.getChart.getXYPlot

    // Init any time series
    val series =
      // Init time series (column, column)
      if(ts.data.isInstanceOf[TypedColumn[_]] &&
        ts.time.isInstanceOf[MonthColumn])
        new MonthSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[MonthColumn],
          ts.data.asInstanceOf[TypedColumn[Number]])
      else if(ts.data.isInstanceOf[TypedColumn[_]] &&
        ts.time.isInstanceOf[DateColumn])
        new DateSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[DateColumn],
          ts.data.asInstanceOf[TypedColumn[Number]])
      else if(ts.data.isInstanceOf[TypedColumn[_]] &&
        ts.time.isInstanceOf[DateTimeColumn])
        new DateTimeSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[DateTimeColumn],
          ts.data.asInstanceOf[TypedColumn[Number]])
      else if(ts.data.isInstanceOf[TypedColumn[_]] &&
        ts.time.isInstanceOf[TimestampColumn])
        new TimestampSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[TimestampColumn],
          ts.data.asInstanceOf[TypedColumn[Number]])
      // Init time series (partitioned column, partitioned column)
      else if(ts.data.isInstanceOf[Array[TypedColumn[_]]] &&
        ts.time.isInstanceOf[Array[MonthColumn]])
        new MonthPartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[MonthColumn]],
          ts.data.asInstanceOf[Array[TypedColumn[Number]]])
      else if(ts.data.isInstanceOf[Array[TypedColumn[_]]] &&
        ts.time.isInstanceOf[Array[DateColumn]])
        new DatePartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[DateColumn]],
          ts.data.asInstanceOf[Array[TypedColumn[Number]]])
      else if(ts.data.isInstanceOf[Array[TypedColumn[_]]] &&
        ts.time.isInstanceOf[Array[DateTimeColumn]])
        new DateTimePartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[DateTimeColumn]],
          ts.data.asInstanceOf[Array[TypedColumn[Number]]])
      else if(ts.data.isInstanceOf[Array[TypedColumn[_]]] &&
        ts.time.isInstanceOf[Array[TimestampColumn]])
        new TimestampPartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[TimestampColumn]],
          ts.data.asInstanceOf[Array[TypedColumn[Number]]])
      // Init time series (column, function)
      else if(ts.data.isInstanceOf[ValueFunction] &&
        ts.time.isInstanceOf[MonthColumn])
        new MonthSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[MonthColumn],
          ts.data.asInstanceOf[ValueFunction])
      else if(ts.data.isInstanceOf[ValueFunction] &&
        ts.time.isInstanceOf[DateColumn])
        new DateSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[DateColumn],
          ts.data.asInstanceOf[ValueFunction])
      else if(ts.data.isInstanceOf[ValueFunction] &&
        ts.time.isInstanceOf[DateTimeColumn])
        new DateTimeSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[DateTimeColumn],
          ts.data.asInstanceOf[ValueFunction])
      else if(ts.data.isInstanceOf[ValueFunction] &&
        ts.time.isInstanceOf[TimestampColumn])
        new TimestampSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[TimestampColumn],
          ts.data.asInstanceOf[ValueFunction])
      // Init time series (partitioned column, function)
      else if(ts.data.isInstanceOf[ValuePartitionFunction] &&
        ts.time.isInstanceOf[Array[MonthColumn]])
        new MonthPartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[MonthColumn]],
          ts.data.asInstanceOf[ValuePartitionFunction])
      else if(ts.data.isInstanceOf[ValuePartitionFunction] &&
        ts.time.isInstanceOf[Array[DateColumn]])
        new DatePartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[DateColumn]],
          ts.data.asInstanceOf[ValuePartitionFunction])
      else if(ts.data.isInstanceOf[ValuePartitionFunction] &&
        ts.time.isInstanceOf[Array[DateTimeColumn]])
        new DateTimePartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[DateTimeColumn]],
          ts.data.asInstanceOf[ValuePartitionFunction])
      else if(ts.data.isInstanceOf[ValuePartitionFunction] &&
        ts.time.isInstanceOf[Array[TimestampColumn]])
        new TimestampPartitionSeries(
          UUID.randomUUID.toString,
          ts.time.asInstanceOf[Array[TimestampColumn]],
          ts.data.asInstanceOf[ValuePartitionFunction])
      else
        throw new Exception("Series type not found")

    val dataset = new TimeSeriesCollection(series)

    val domainAxis =
      if(axes.hold)
        plot.getDomainAxis
      else
        new DateAxis

    val rangeAxis =
      if(axes.hold)
        plot.getRangeAxis
      else
        new NumberAxis

    val renderer = new XYLineAndShapeRenderer(true, false) {

      // Disable legend by default
      setSeriesVisibleInLegend(0, false)

    }

    // Get default color
    var color = gcf.default.axes.colorOrder(
      (dataset.getSeriesCount - 1) % gcf.default.axes.colorOrder.size)

    var i = 0
    var fromRecord = -1
    var tillRecord = -1

    while(i < values.length) {

      values(i) match {

        case "Color" =>

          i += 1

          // Overwrite default color
          color = values(i).asInstanceOf[Color]

        case "From" =>

          i += 1

          val record = values(i).asInstanceOf[Int]

          fromRecord = record
          domainAxis.setLowerBound(
            series.getTimePeriod(record).getStart.getTime)

        case "Till" =>

          i += 1

          val record = values(i).asInstanceOf[Int]

          tillRecord = record
          domainAxis.setUpperBound(
            series.getTimePeriod(record).getStart.getTime)

        case linespec: String => // Is not a property but linespec

          val linespec = values(i).asInstanceOf[String]

          val color = """[0-6|k|r|g|b|m|c|w]""".r
            .findAllIn(linespec).toSeq.lastOption

          if(!color.isEmpty)
            color.get match {

              case "0" | "k" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.BLACK)
              case "1" | "r" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.RED)
              case "2" | "g" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.GREEN)
              case "3" | "b" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.BLUE)
              case "4" | "m" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.MAGENTA)
              case "5" | "c" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.CYAN)
              case "6" | "w" => renderer.setSeriesPaint(dataset.getSeriesCount - 1, Color.WHITE)

            }

          val line = """-""".r.findAllIn(linespec).toSeq.lastOption
          val style = """[.|+|*|o|x|^]""".r.findAllIn(linespec).toSeq.lastOption

          if(!style.isEmpty && style.get != "-") {

            if(line.isEmpty)
              renderer.setSeriesLinesVisible(dataset.getSeriesCount - 1, false)

            style.get match {

              case "." => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                widebase.ui.chart.util.ShapeUtilities.createDot(1.0f))

              case "+" => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                widebase.ui.chart.util.ShapeUtilities.createRegularCross(6.0f))

              case "*" => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                widebase.ui.chart.util.ShapeUtilities.createDiagonalCross(
                  6.0f,
                  widebase.ui.chart.util.ShapeUtilities.createRegularCross(6.0f)))

              case "o" => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                widebase.ui.chart.util.ShapeUtilities.createCircle(6.0f))

              case "x" => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                widebase.ui.chart.util.ShapeUtilities.createDiagonalCross(6.0f))

              case "^" => renderer.setSeriesShape(
                dataset.getSeriesCount - 1,
                ShapeUtilities.createUpTriangle(6.0f))

            }

            if(renderer.isInstanceOf[XYLineAndShapeRenderer])
              renderer.asInstanceOf[XYLineAndShapeRenderer]
                .setSeriesShapesVisible(dataset.getSeriesCount - 1, true)

          }

        case _ => throw new Exception("Property not found: " + values(i))

      }

      i += 1

    }

    // Set color
    renderer.setSeriesPaint(dataset.getSeriesCount - 1, color)

    if(axes.hold) {

      plot.setDataset(plot.getDatasetCount, dataset)
      plot.setRenderer(plot.getRendererCount, renderer)

    } else {

      // Fix lower bound if only upper bound was set
      if(fromRecord == -1 && tillRecord != -1) {

        fromRecord = 0
        domainAxis.setLowerBound(series.getTimePeriod(fromRecord).getStart.getTime)

      }

      // Fix upper bound if only lower bound was set
      if(fromRecord != -1 && tillRecord == -1) {

        tillRecord = series.getItemCount - 1
        domainAxis.setUpperBound(series.getTimePeriod(tillRecord).getStart.getTime)

      }

      var maxY = Double.MinValue
      var minY = Double.MaxValue

      for(i <- 0 to dataset.getSeriesCount - 1) {

        val series = dataset.getSeries(i)

        val _tillRecord =
          if(tillRecord == -1 || series.getItemCount < tillRecord)
            series.getItemCount - 1
          else
            tillRecord

        val _fromRecord =
          if(fromRecord == -1)
            0
          else
            fromRecord

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

      plot.setDataset(0, dataset)
      plot.setDomainAxis(0, domainAxis)
      plot.setRangeAxis(0, rangeAxis)
      plot.setRenderer(0, renderer)

    }

    plot

  }
}

