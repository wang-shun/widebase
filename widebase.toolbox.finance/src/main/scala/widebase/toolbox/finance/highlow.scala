package widebase.toolbox.finance

import java.awt.Color
import java.util.UUID

import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot

import org.jfree.chart.renderer.xy.HighLowRenderer
import org.jfree.data.time.ohlc. { OHLCItem, OHLCSeriesCollection }

import widebase.db.column. {

  MonthColumn,
  DateColumn,
  DateTimeColumn,
  DoubleColumn,
  TimestampColumn

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

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Highlow plot.
 *
 * @author myst3r10n
 */
object highlow {

  import widebase.toolbox.core.graphics.gca

  /** Plot highlow into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param high values
   * @param low values
   * @param close values
   * @param open values
   * @param color of highlow
   * @param dates of highlow
   * @param properties of highlow
   *
   * @return plot handle
   */
  def apply(
    high: Any,
    low: Any,
    close: Any,
    open: Any,
    color: Color,
    dates: Any,
    properties: Any*): XYPlot =
    highlow(
      gca,
      high,
      low,
      close,
      open,
      color,
      dates,
      properties:_*)

  /** Plot highlow into specific axes.
   *
   * @param axes handle
   * @param high values
   * @param low values
   * @param close values
   * @param open values
   * @param color of highlow
   * @param dates of highlow
   * @param properties of highlow
   *
   * @return plot handle
   */
  def apply(
    axes: AxesPanel,
    high: Any,
    low: Any,
    close: Any,
    open: Any,
    color: Color,
    dates: Any,
    properties: Any*) = {

    if(!axes.peer.getChart.getPlot.isInstanceOf[XYPlot])
      axes.peer.setChart(AxesPanel.default.chart(new XYPlot))

    val plot = axes.peer.getChart.getXYPlot

    // Init any time series
    val series =
      // Init time series (columns...)
      if(dates.isInstanceOf[MonthColumn])
        new widebase.ui.chart.data.time.ohlc.MonthSeries(
          dates.asInstanceOf[MonthColumn],
          open.asInstanceOf[DoubleColumn],
          high.asInstanceOf[DoubleColumn],
          low.asInstanceOf[DoubleColumn],
          close.asInstanceOf[DoubleColumn],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[DateColumn])
        new DateSeries(
          dates.asInstanceOf[DateColumn],
          open.asInstanceOf[DoubleColumn],
          high.asInstanceOf[DoubleColumn],
          low.asInstanceOf[DoubleColumn],
          close.asInstanceOf[DoubleColumn],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[DateTimeColumn])
        new DateTimeSeries(
          dates.asInstanceOf[DateTimeColumn],
          open.asInstanceOf[DoubleColumn],
          high.asInstanceOf[DoubleColumn],
          low.asInstanceOf[DoubleColumn],
          close.asInstanceOf[DoubleColumn],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[TimestampColumn])
        new TimestampSeries(
          dates.asInstanceOf[TimestampColumn],
          open.asInstanceOf[DoubleColumn],
          high.asInstanceOf[DoubleColumn],
          low.asInstanceOf[DoubleColumn],
          close.asInstanceOf[DoubleColumn],
          UUID.randomUUID.toString)
      // Init time series (partitioned columns)
      else if(dates.isInstanceOf[Array[MonthColumn]])
        new MonthPartitionSeries(
          dates.asInstanceOf[Array[MonthColumn]],
          open.asInstanceOf[Array[DoubleColumn]],
          high.asInstanceOf[Array[DoubleColumn]],
          low.asInstanceOf[Array[DoubleColumn]],
          close.asInstanceOf[Array[DoubleColumn]],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[Array[DateColumn]])
        new DatePartitionSeries(
          dates.asInstanceOf[Array[DateColumn]],
          open.asInstanceOf[Array[DoubleColumn]],
          high.asInstanceOf[Array[DoubleColumn]],
          low.asInstanceOf[Array[DoubleColumn]],
          close.asInstanceOf[Array[DoubleColumn]],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[Array[DateTimeColumn]])
        new DateTimePartitionSeries(
          dates.asInstanceOf[Array[DateTimeColumn]],
          open.asInstanceOf[Array[DoubleColumn]],
          high.asInstanceOf[Array[DoubleColumn]],
          low.asInstanceOf[Array[DoubleColumn]],
          close.asInstanceOf[Array[DoubleColumn]],
          UUID.randomUUID.toString)
      else if(dates.isInstanceOf[Array[TimestampColumn]])
        new TimestampPartitionSeries(
          dates.asInstanceOf[Array[TimestampColumn]],
          open.asInstanceOf[Array[DoubleColumn]],
          high.asInstanceOf[Array[DoubleColumn]],
          low.asInstanceOf[Array[DoubleColumn]],
          close.asInstanceOf[Array[DoubleColumn]],
          UUID.randomUUID.toString)
      else
        throw new Exception("Series type not found")

    var dataset = new OHLCSeriesCollection { addSeries(series) }

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

    val renderer = new HighLowRenderer {

      // Disable legend by default
      setSeriesVisibleInLegend(0, false)

    }

    var i = 0
    var fromRecord = -1
    var tillRecord = -1

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      property match {

        case "From" =>

          val record = properties(i).asInstanceOf[Int]

          fromRecord = record
          domainAxis.setLowerBound(series.getPeriod(record).getStart.getTime)

        case "Till" =>

          val record = properties(i).asInstanceOf[Int]

          tillRecord = record
          domainAxis.setUpperBound(series.getPeriod(record).getStart.getTime)

        case _ => throw new Exception("Property not found: " + property)

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
        domainAxis.setLowerBound(series.getPeriod(fromRecord).getStart.getTime)

      }

      // Fix upper bound if only lower bound was set
      if(fromRecord != -1 && tillRecord == -1) {

        tillRecord = series.getItemCount - 1
        domainAxis.setUpperBound(series.getPeriod(tillRecord).getStart.getTime)

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

          val values = series.getDataItem(i).asInstanceOf[OHLCItem]

          if(maxY < values.getHighValue)
            maxY = values.getHighValue

          if(minY > values.getLowValue)
            minY = values.getLowValue

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

