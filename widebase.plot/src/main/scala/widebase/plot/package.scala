package widebase

import plot.ui.PlotFrame

import java.awt.geom.Point2D
import java.lang.management.ManagementFactory

import morechart.chart.ShiftableChartPanel

import org.jfree.chart. { ChartPanel, JFreeChart }
import org.jfree.chart.axis. { DateAxis, NumberAxis }
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.time.TimeSeriesCollection

import scala.collection.JavaConversions
import scala.swing. { Frame, Publisher, SimpleSwingApplication }
import scala.swing.event.WindowClosing

/** Plot package.
 *
 * @author myst3r10n
 */
package object plot {

  /** Plot XY data.
   *
   * @param collection with data series
   * @param records limitation by first view
   */
  def xy(collection: TimeSeriesCollection, shift: Int = 0, records: Int = -1) {

    val domainAxis = new DateAxis {

      protected val head = collection.getSeries(0)

      setLowerBound(head.getTimePeriod(shift).getStart.getTime)

      if(records == -1 || head.getItemCount - 1 < records)
        setUpperBound(head.getTimePeriod(head.getItemCount - 1 - shift).getStart.getTime)
      else
        setUpperBound(head.getTimePeriod(records - shift).getStart.getTime)

    }

    val rangeAxis = new NumberAxis {

      protected var maxY = Double.MinValue
      protected var minY = Double.MaxValue

      for(i <- 0 to collection.getSeriesCount - 1) {

        val series = collection.getSeries(i)

        val length =
          if(records == -1 || series.getItemCount < records)
            series.getItemCount
          else
            records

        for(i <- 0 to length - 1) {

          val value = series.getDataItem(i).getValue.doubleValue

          if(maxY < value)
            maxY = value

          if(minY > value)
            minY = value

        }
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

    val plotPanel = new ChartPanel(chart) with Publisher with ShiftableChartPanel {

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

    show(new PlotFrame(plotPanel))

  }

  protected class SwingApp(top0: Frame) extends SimpleSwingApplication {

    def top = top0

    startup(scala.collection.JavaConversions.collectionAsScalaIterable(
      ManagementFactory.getRuntimeMXBean.getInputArguments).toArray)

  }

  /** Show plot.
   *
   * @param top component
   */
  protected def show(top: Frame) {

      top.reactions += {

        case WindowClosing(source) => source.dispose
          
      }

      top.pack
      top.visible = true

  }
}

