package widebase.ui.chart.plot

import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy. { AbstractXYItemRenderer, HighLowRenderer }
import org.jfree.data.time.ohlc. { OHLCItem, OHLCSeriesCollection }

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks. { break, breakable }

import widebase.db.column. {

  DoubleColumn,
  MonthColumn,
  DateColumn,
  DateTimeColumn,
  TimestampColumn,

  TypedColumn

}

import widebase.ui.chart.data.time.ohlc. {

  DatePartitionSeries,
  DateSeries,
  DateTimePartitionSeries,
  DateTimeSeries,
  MonthPartitionSeries,
  MonthSeries,
  TimestampPartitionSeries,
  TimestampSeries

}

/** Highlow Plot.
 *
 * @author myst3r10n
 */
object Highlow {

  /** Add plot.
   *
   * @param values of data, properties and format
   * @param renderer of chart
   *
   * @return plot
   */
  def add(plot: org.jfree.chart.plot.Plot, values: Any*)
    (implicit renderer: AbstractXYItemRenderer = new HighLowRenderer) = {

    val overlay = this(values:_*)
    val current = plot.asInstanceOf[XYPlot]

    current.setDataset(current.getDatasetCount, overlay.getDataset)
    current.setRenderer(current.getRendererCount, overlay.getRenderer)

    current

  }

  /** Add plot with second Y axis.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def addYY(plot: org.jfree.chart.plot.Plot, values: Any*)
    (implicit renderer: AbstractXYItemRenderer = new HighLowRenderer) = {

    val overlay = this(values:_*)
    val current = plot.asInstanceOf[XYPlot]

    current.setRangeAxis(1, overlay.getRangeAxis)
    current.setDataset(current.getDatasetCount, overlay.getDataset)
    current.mapDatasetToRangeAxis(1, 1);
    current.setRenderer(current.getRendererCount, overlay.getRenderer)

    current

  }

  /** Perform highlow.
   *
   * @param values of data, properties and format
   * @param renderer of chart
   *
   * @return highlow plot
   */
  def apply(values: Any*)(implicit renderer: AbstractXYItemRenderer = new HighLowRenderer) = {

    val collection = new OHLCSeriesCollection
    val domainAxis = new DateAxis
    val rangeAxis = new NumberAxis

    var fromRecord = ArrayBuffer[Int]()
    var tillRecord = ArrayBuffer[Int]()

    var i = 0

    while(i < values.length) {

      fromRecord += -1
      tillRecord += -1

      // Init any time series or if not found a xy series
      val series =
        // Init time series (columns...)
        if(values(i).isInstanceOf[MonthColumn])
          new widebase.ui.chart.data.time.ohlc.MonthSeries(
            values(i).asInstanceOf[MonthColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i + 4).asInstanceOf[DoubleColumn],
            "")
        else if(values(i).isInstanceOf[DateColumn])
          new DateSeries(
            values(i).asInstanceOf[DateColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i + 4).asInstanceOf[DoubleColumn],
            "")
        else if(values(i).isInstanceOf[DateTimeColumn])
          new DateTimeSeries(
            values(i).asInstanceOf[DateTimeColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i + 4).asInstanceOf[DoubleColumn],
            "")
        else if(values(i).isInstanceOf[TimestampColumn])
          new TimestampSeries(
            values(i).asInstanceOf[TimestampColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i + 4).asInstanceOf[DoubleColumn],
            "")
        // Init time series (partitioned columns)
        else if(values(i).isInstanceOf[Array[MonthColumn]])
          new MonthPartitionSeries(
            values(i).asInstanceOf[Array[MonthColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i).isInstanceOf[Array[DateColumn]])
          new DatePartitionSeries(
            values(i).asInstanceOf[Array[DateColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i).isInstanceOf[Array[DateTimeColumn]])
          new DateTimePartitionSeries(
            values(i).asInstanceOf[Array[DateTimeColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i).isInstanceOf[Array[TimestampColumn]])
          new TimestampPartitionSeries(
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        // Init time series (columns..., Octave compatibility)
        else if(values(i + 4).isInstanceOf[MonthColumn])
          new widebase.ui.chart.data.time.ohlc.MonthSeries(
            values(i + 4).asInstanceOf[MonthColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i).asInstanceOf[DoubleColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            "")
        else if(values(i + 4).isInstanceOf[DateColumn])
          new DateSeries(
            values(i + 4).asInstanceOf[DateColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i).asInstanceOf[DoubleColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            "")
        else if(values(i + 4).isInstanceOf[DateTimeColumn])
          new DateTimeSeries(
            values(i + 4).asInstanceOf[DateTimeColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i).asInstanceOf[DoubleColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            "")
        else if(values(i + 4).isInstanceOf[TimestampColumn])
          new TimestampSeries(
            values(i + 4).asInstanceOf[TimestampColumn],
            values(i + 3).asInstanceOf[DoubleColumn],
            values(i).asInstanceOf[DoubleColumn],
            values(i + 1).asInstanceOf[DoubleColumn],
            values(i + 2).asInstanceOf[DoubleColumn],
            "")
        // Init time series (partitioned columns..., Octave compatibility)
        else if(values(i + 4).isInstanceOf[Array[MonthColumn]])
          new MonthPartitionSeries(
            values(i + 4).asInstanceOf[Array[MonthColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i).asInstanceOf[Array[DoubleColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i + 4).isInstanceOf[Array[DateColumn]])
          new DatePartitionSeries(
            values(i + 4).asInstanceOf[Array[DateColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i).asInstanceOf[Array[DoubleColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i + 4).isInstanceOf[Array[DateTimeColumn]])
          new DateTimePartitionSeries(
            values(i + 4).asInstanceOf[Array[DateTimeColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i).asInstanceOf[Array[DoubleColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            "")
        else
          new TimestampPartitionSeries(
            values(i + 4).asInstanceOf[Array[TimestampColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i).asInstanceOf[Array[DoubleColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            "")

      i += 5

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
              HighlowProperty(series, domainAxis, property, values(i))

              i += 1

            // Or a format string?
            } else {

              // Format chart
              HighlowFormat(
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

      // Fix lower bound if only upper bound was set
      if(fromRecord.last == -1 && tillRecord.last != -1) {

        fromRecord(collection.getSeriesCount) = 0

        domainAxis.setLowerBound(series.getPeriod(
          fromRecord(collection.getSeriesCount)).getStart.getTime)

      }

      // Fix upper bound if only lower bound was set
      if(fromRecord.last != -1 && tillRecord.last == -1) {

        tillRecord(collection.getSeriesCount) = series.getItemCount - 1

        domainAxis.setUpperBound(series.getPeriod(
          tillRecord(collection.getSeriesCount)).getStart.getTime)

      }

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

        val values = series.getDataItem(i).asInstanceOf[OHLCItem]

        if(maxY < values.getHighValue)
          maxY = values.getHighValue

        if(minY > values.getLowValue)
          minY = values.getLowValue

      }
    }

    rangeAxis.setUpperBound(maxY)
    rangeAxis.setLowerBound(minY)

    new XYPlot(collection, domainAxis, rangeAxis, renderer)

  }
}

