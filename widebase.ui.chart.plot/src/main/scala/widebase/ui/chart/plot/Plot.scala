package widebase.ui.chart.plot

import org.jfree.chart.axis. { DateAxis, NumberAxis, ValueAxis }
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer

import org.jfree.data.time. { TimeSeriesCollection, TimeSeriesWorkaround }

import org.jfree.data.xy. {

  AbstractXYDataset,
  XYSeriesCollection,
  XYSeriesWorkaround

}

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks. { break, breakable }

import widebase.db.column. {

  MonthColumn,
  DateColumn,
  DateTimeColumn,
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

import widebase.ui.chart.data.xy. { XYPartitionSeries, XYSeries }

/** Plot.
 *
 * @author myst3r10n
 */
object Plot {

  /** Add plot.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def add(plot: org.jfree.chart.plot.Plot, values: Any*) = {

    val overlay = this(values:_*)
    val current = plot.asInstanceOf[XYPlot]

    current.setDataset(current.getDatasetCount, overlay.getDataset)
    current.setRenderer(current.getRendererCount, overlay.getRenderer)

    current

  }

  /** Perform plot.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def apply(values: Any*) = {

    var collection: AbstractXYDataset = null
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
        // Init time series (column, column)
        if(values(i).isInstanceOf[MonthColumn] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new MonthSeries(
            "",
            values(i).asInstanceOf[MonthColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        else if(values(i).isInstanceOf[DateColumn] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new DateSeries(
            "",
            values(i).asInstanceOf[DateColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        else if(values(i).isInstanceOf[DateTimeColumn] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new DateTimeSeries(
            "",
            values(i).asInstanceOf[DateTimeColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        else if(values(i).isInstanceOf[TimestampColumn] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new TimestampSeries(
            "",
            values(i).asInstanceOf[TimestampColumn],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        // Init time series (partitioned column, partitioned column)
        else if(values(i).isInstanceOf[Array[MonthColumn]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new MonthPartitionSeries(
            "",
            values(i).asInstanceOf[Array[MonthColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        else if(values(i).isInstanceOf[Array[DateColumn]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new DatePartitionSeries(
            "",
            values(i).asInstanceOf[Array[DateColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        else if(values(i).isInstanceOf[Array[DateTimeColumn]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new DateTimePartitionSeries(
            "",
            values(i).asInstanceOf[Array[DateTimeColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        else if(values(i).isInstanceOf[Array[TimestampColumn]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new TimestampPartitionSeries(
            "",
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        // Init time series (column, function)
        else if(values(i).isInstanceOf[MonthColumn] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new MonthSeries(
            "",
            values(i).asInstanceOf[MonthColumn],
            values(i + 1).asInstanceOf[ValueFunction])
        else if(values(i).isInstanceOf[DateColumn] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new DateSeries(
            "",
            values(i).asInstanceOf[DateColumn],
            values(i + 1).asInstanceOf[ValueFunction])
        else if(values(i).isInstanceOf[DateTimeColumn] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new DateTimeSeries(
            "",
            values(i).asInstanceOf[DateTimeColumn],
            values(i + 1).asInstanceOf[ValueFunction])
        else if(values(i).isInstanceOf[TimestampColumn] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new TimestampSeries(
            "",
            values(i).asInstanceOf[TimestampColumn],
            values(i + 1).asInstanceOf[ValueFunction])
        // Init time series (partitioned column, function)
        else if(values(i).isInstanceOf[Array[MonthColumn]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new MonthPartitionSeries(
            "",
            values(i).asInstanceOf[Array[MonthColumn]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        else if(values(i).isInstanceOf[Array[DateColumn]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new DatePartitionSeries(
            "",
            values(i).asInstanceOf[Array[DateColumn]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        else if(values(i).isInstanceOf[Array[DateTimeColumn]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new DateTimePartitionSeries(
            "",
            values(i).asInstanceOf[Array[DateTimeColumn]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        else if(values(i).isInstanceOf[Array[TimestampColumn]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new TimestampPartitionSeries(
            "",
            values(i).asInstanceOf[Array[TimestampColumn]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        // Init xy series (column, column)
        else if(values(i).isInstanceOf[TypedColumn[_]] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new XYSeries(
            "",
            values(i).asInstanceOf[TypedColumn[Number]],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        // Init xy series (partitioned column, partitioned column)
        else if(values(i).isInstanceOf[Array[TypedColumn[_]]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new XYPartitionSeries(
            "",
            values(i).asInstanceOf[Array[TypedColumn[Number]]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        // Init xy series (column, function)
        else if(values(i).isInstanceOf[TypedColumn[_]] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new XYSeries(
            "",
            values(i).asInstanceOf[TypedColumn[Number]],
            values(i + 1).asInstanceOf[ValueFunction])
        // Init xy series (partitioned column, function)
        else if(values(i).isInstanceOf[Array[TypedColumn[_]]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new XYPartitionSeries(
            "",
            values(i).asInstanceOf[Array[TypedColumn[Number]]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        else
          throw new Exception("Series type not found")

      // Init series collection
      if(collection == null)
        // A time based series
        if(series.isInstanceOf[TimeSeriesWorkaround]) {

          collection = new TimeSeriesCollection
          domainAxis = new DateAxis

        // A xy based series
        } else if(series.isInstanceOf[XYSeriesWorkaround]) {

          collection = new XYSeriesCollection
          domainAxis = new NumberAxis

        }

      i += 2

      breakable {

        while(i < values.length)
          if(i < values.length && values(i).isInstanceOf[String]) {

            // Is property pair?
            if(i + 1 < values.length &&
              !values(i + 1).isInstanceOf[ValueFunction] &&
              !values(i + 1).isInstanceOf[ValuePartitionFunction] &&
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
              PlotProperty(series, domainAxis, property, values(i))

              i += 1

            // Or a format string?
            } else {

              // Format chart
              PlotFormat(
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
      } else if(series.isInstanceOf[XYSeriesWorkaround]) {

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

    new XYPlot(collection, domainAxis, rangeAxis, renderer)

  }
}

