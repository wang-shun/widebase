package widebase.plot.ui

import java.awt.Color
import java.awt.geom.Point2D

import morechart.chart.ShiftableChartPanel

import org.jfree.chart. {

  ChartMouseEvent,
  ChartMouseListener,
  ChartPanel,
  JFreeChart

}

import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.time.TimeSeriesCollection

import org.joda.time.LocalDateTime

import scala.swing. { Point, Publisher }

case class PlotPanel(
  collection: TimeSeriesCollection)
  extends ChartPanel(null)
  with Publisher
  with ShiftableChartPanel {

  import BigDecimal.RoundingMode

  val domainAxis = new DateAxis {

    setLowerBound(collection.getSeries(0).getTimePeriod(0).getStart.getTime)

    if(collection.getSeries(0).getItemCount - 1 < 24)
      setUpperBound(collection.getSeries(0).getTimePeriod(
        collection.getSeries(0).getItemCount - 1).getStart.getTime)
    else
      setUpperBound(collection.getSeries(0).getTimePeriod(24).getStart.getTime)

  }

  val rangeAxis = new NumberAxis {

    val length =
      if(collection.getSeries(0).getItemCount < 24)
        collection.getSeries(0).getItemCount
      else
        24

    var maxY = Double.MinValue
    var minY = Double.MaxValue

    for(i <- 0 to length - 1) {

      val value = collection.getSeries(0).getDataItem(i).getValue.doubleValue

      if(maxY < value)
        maxY = value

      if(minY > value)
        minY = value

    }

    setUpperBound(maxY)
    setLowerBound(minY)

  }

  val renderer = new XYLineAndShapeRenderer(true, true)
  val plot = new XYPlot(collection, domainAxis, rangeAxis, renderer)

  val chart = new JFreeChart(plot) {

    setAntiAlias(false)
    setTextAntiAlias(false)

  }

  setChart(chart)

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

