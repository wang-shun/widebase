package widebase.ui.chart.plot

import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy. { AbstractXYItemRenderer, HighLowRenderer }
import org.jfree.data.time.ohlc.OHLCSeriesCollection

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

  DateSeries,
  DateSeriesParted,
  DateTimeSeries,
  DateTimeSeriesParted,
  MonthSeries,
  MonthSeriesParted,
  TimestampSeries,
  TimestampSeriesParted

}

/** Highlow Plot.
 *
 * @author myst3r10n
 */
object Highlow {

  /** Perform highlow.
   *
   * @param values of data, properties and format
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
        // Init time series
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
        // Init time series (partitioned table)
        else if(values(i).isInstanceOf[Array[MonthColumn]])
          new MonthSeriesParted(
            values(i).asInstanceOf[Array[MonthColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i).isInstanceOf[Array[DateColumn]])
          new DateSeriesParted(
            values(i).asInstanceOf[Array[DateColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else if(values(i).isInstanceOf[Array[DateTimeColumn]])
          new DateTimeSeriesParted(
            values(i).asInstanceOf[Array[DateTimeColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
            "")
        else
          new TimestampSeriesParted(
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[Array[DoubleColumn]],
            values(i + 2).asInstanceOf[Array[DoubleColumn]],
            values(i + 3).asInstanceOf[Array[DoubleColumn]],
            values(i + 4).asInstanceOf[Array[DoubleColumn]],
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

    new XYPlot(collection, domainAxis, rangeAxis, renderer)

  }
}

