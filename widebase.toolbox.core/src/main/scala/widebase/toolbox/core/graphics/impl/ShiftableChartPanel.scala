package widebase.toolbox.core.graphics.impl

import java.awt.event. {

  MouseEvent,
  MouseMotionListener,
  MouseWheelEvent,
  MouseWheelListener

}

import java.awt.geom. { Point2D, Rectangle2D }

import javax.swing.event.MouseInputListener

import org.jfree.data.RangeType
import org.jfree.chart.axis. { NumberAxis, ValueAxis }

import org.jfree.chart.plot. {

  CombinedDomainXYPlot,
  CombinedRangeXYPlot,
  FastScatterPlot,
  Plot,
  XYPlot

}

import org.jfree.ui.RectangleEdge

import scala.collection.JavaConversions
import scala.collection.mutable. { Buffer, Map }

/** Extends [[org.jfree.chart.ChartPanel]] with x and y shift.
 *
 * Shift mode can be bounded with [[org.jfree.data.RangeType]].
 *
 * @author myst3r10n
 */
trait ShiftableChartPanel extends org.jfree.chart.ChartPanel {

  import widebase.toolbox.core.graphics.gcf

  private object shift {

    var plot: Plot = null
    var dataArea: Rectangle2D = null
    var point: Point2D.Double = null

  }

  /** Shiftable plots and it's interval. */
  protected var shiftable = Map[Plot, Point2D.Double]()

  /** Shiftable by mouse drag. */
  def mouseDragShiftable = gcf.toolBar.pan.selected

  /** Shiftable by mouse wheel. */
  var mouseWheelShiftable = false

  addMouseListener(new MouseInputListener{

    def mousePressed(event: MouseEvent) {

      event.getButton match {

        case MouseEvent.BUTTON1 =>

          if(getChart.getPlot.isInstanceOf[XYPlot]) {

            val plot = getChart.getPlot.asInstanceOf[XYPlot]

            shiftable(getChart.getPlot) = new Point2D.Double(
              (plot.getDomainAxis.getUpperBound -
                 plot.getDomainAxis.getLowerBound) / 1000,
              (plot.getRangeAxis.getUpperBound -
                 plot.getRangeAxis.getLowerBound) / 1000)

          } else if(getChart.getPlot.isInstanceOf[FastScatterPlot]) {

            val plot = getChart.getPlot.asInstanceOf[FastScatterPlot]

            shiftable(getChart.getPlot) = new Point2D.Double(
              (plot.getDomainAxis.getUpperBound -
                 plot.getDomainAxis.getLowerBound) / 1000,
              (plot.getRangeAxis.getUpperBound -
                 plot.getRangeAxis.getLowerBound) / 1000)

          }

          // Shiftable.
          if(mouseDragShiftable) {

            var plot: Plot = null

            if(getChartRenderingInfo.getPlotInfo.getSubplotCount == 0 &&
               getChartRenderingInfo.getPlotInfo.getDataArea.contains(event.getPoint)) {

              plot = getChart.getPlot
              shift.dataArea = getChartRenderingInfo.getPlotInfo.getDataArea
 
            } else {

              object Break extends Throwable
 
              try {

                for(i <- 0 to getChartRenderingInfo.getPlotInfo.getSubplotCount - 1)
                  if(getChartRenderingInfo.getPlotInfo
                    .getSubplotInfo(i).getDataArea.contains(event.getPoint)) {

                    plot =
                      if(getChart.getPlot.isInstanceOf[CombinedDomainXYPlot])
                        JavaConversions.asScalaBuffer(getChart.getPlot
                          .asInstanceOf[CombinedDomainXYPlot].getSubplots)
                          .asInstanceOf[Buffer[XYPlot]](i)
                      else if(getChart.getPlot.isInstanceOf[CombinedRangeXYPlot])
                        JavaConversions.asScalaBuffer(getChart.getPlot
                          .asInstanceOf[CombinedRangeXYPlot].getSubplots)
                          .asInstanceOf[Buffer[XYPlot]](i)
                      else
                        null

                    shift.dataArea =
                      if(getChart.getPlot.isInstanceOf[CombinedDomainXYPlot])
                        getChartRenderingInfo.getPlotInfo
                          .getSubplotInfo(i).getDataArea
                      else if(getChart.getPlot.isInstanceOf[CombinedRangeXYPlot])
                        getChartRenderingInfo.getPlotInfo
                          .getSubplotInfo(i).getDataArea
                      else
                        null

                    throw Break

                  }

              } catch { case Break => }

            }

            if(shiftable.contains(plot)) {

              shift.plot = plot

              shift.point = new Point2D.Double(
                event.getPoint.getX,
                event.getPoint.getY)


            }
          }

        case _ =>
      }
    }

    def mouseReleased(event: MouseEvent) {

      if(shift.plot != null)
        shift.plot = null

    }

    def mouseMoved(event: MouseEvent) {}
    def mouseDragged(event: MouseEvent) {}
    def mouseExited(event: MouseEvent) {}
    def mouseEntered(event: MouseEvent) {}
    def mouseClicked(event: MouseEvent) {}

  } )

  addMouseMotionListener(new MouseMotionListener {

    def mouseDragged(event: MouseEvent) {

      // Shiftable.
      if(mouseDragShiftable &&
        !isDomainZoomable &&
         !isRangeZoomable &&
         shift.plot != null) {

        // Shift domain.
        if(shiftable(shift.plot).x >= 0.0 &&
           shift.point.x != event.getX)
          if(shift.plot.isInstanceOf[FastScatterPlot]) {

            val plot = shift.plot.asInstanceOf[FastScatterPlot]

            shifting(shift.dataArea,
                     plot.getDomainAxis,
                     RectangleEdge.BOTTOM,
                     shiftable(plot).x,
                     shift.point.x,
                     event.getPoint.getX)

          } else if(shift.plot.isInstanceOf[XYPlot]) {

            val plot = shift.plot.asInstanceOf[XYPlot]

            for(i <- 0 to plot.getDomainAxisCount - 1)
              shifting(shift.dataArea,
                       plot.getDomainAxis(i),
                       plot.getDomainAxisEdge(i),
                       if(i == 0) shiftable(plot).x else 0,
                       shift.point.x,
                       event.getPoint.getX)

          } else
            throw new Exception("Plot unsupported")

        // Shift range.
        if(shiftable(shift.plot).y >= 0.0 &&
           shift.point.y != event.getY)
          if(shift.plot.isInstanceOf[FastScatterPlot]) {

            val plot = shift.plot.asInstanceOf[FastScatterPlot]

            shifting(shift.dataArea,
                     plot.getRangeAxis,
                     RectangleEdge.LEFT,
                     shiftable(shift.plot).y,
                     shift.point.y,
                     event.getPoint.getY)

          } else if(shift.plot.isInstanceOf[XYPlot]) {

            val plot = shift.plot.asInstanceOf[XYPlot]

            for(i <- 0 to plot.getRangeAxisCount - 1)
              shifting(shift.dataArea,
                       plot.getRangeAxis(i),
                       plot.getRangeAxisEdge(i),
                       if(i == 0) shiftable(plot).y else 0,
                       shift.point.y,
                       event.getPoint.getY)

          } else
            throw new Exception("Plot unsupported")

        shift.point = new Point2D.Double(event.getPoint.getX, event.getPoint.getY)

      }
    }

    def mouseMoved(event: MouseEvent) {}

  } )

  addMouseWheelListener(new MouseWheelListener {

    def mouseWheelMoved(event: MouseWheelEvent) {

      if(mouseWheelShiftable) {

        var plot = getChart.getPlot
        var dataArea = getChartRenderingInfo.getPlotInfo.getDataArea

        shiftable(getChart.getPlot) = new Point2D.Double(
          (getChart.getPlot.asInstanceOf[XYPlot].getDomainAxis.getUpperBound -
             getChart.getPlot.asInstanceOf[XYPlot].getDomainAxis.getLowerBound),
          (getChart.getPlot.asInstanceOf[XYPlot].getRangeAxis.getUpperBound -
            getChart.getPlot.asInstanceOf[XYPlot].getRangeAxis.getLowerBound))

        object Break extends Throwable

        try {

          for(i <- 0 to getChartRenderingInfo.getPlotInfo.getSubplotCount - 1)
            if(getChartRenderingInfo.getPlotInfo
              .getSubplotInfo(i).getDataArea.contains(event.getPoint)) {

              plot =
                if(getChart.getPlot.isInstanceOf[CombinedDomainXYPlot])
                  JavaConversions.asScalaBuffer(getChart.getPlot
                    .asInstanceOf[CombinedDomainXYPlot].getSubplots)
                    .asInstanceOf[Buffer[XYPlot]](i)
                else if(getChart.getPlot.isInstanceOf[CombinedRangeXYPlot])
                  JavaConversions.asScalaBuffer(getChart.getPlot
                    .asInstanceOf[CombinedRangeXYPlot].getSubplots)
                    .asInstanceOf[Buffer[XYPlot]](i)
                else
                  null

              dataArea =
                if(getChart.getPlot.isInstanceOf[CombinedDomainXYPlot])
                  getChartRenderingInfo.getPlotInfo
                    .getSubplotInfo(i).getDataArea
                else if(getChart.getPlot.isInstanceOf[CombinedRangeXYPlot])
                  getChartRenderingInfo.getPlotInfo
                    .getSubplotInfo(i).getDataArea
                else
                  null

              throw Break

            }

        } catch { case Break => }

        // Shiftable.
        if(!isDomainZoomable && !isRangeZoomable && plot != null) {
          if(shiftable(plot).x >= 0.0 && event.getScrollAmount != 0) {

            val offset =
              if(event.getUnitsToScroll < 0)
                -shiftable(plot).x
              else
                shiftable(plot).x

            if(plot.isInstanceOf[FastScatterPlot]) {

              val nativePlot = plot.asInstanceOf[FastScatterPlot]

              shifting(dataArea,
                       nativePlot.getDomainAxis,
                       RectangleEdge.BOTTOM,
                       0.0,
                       event.getPoint.getX,
                       event.getPoint.getX + offset)

            } else if(plot.isInstanceOf[XYPlot]) {

              val nativePlot = plot.asInstanceOf[XYPlot]

              for(i <- 0 to nativePlot.getDomainAxisCount - 1)
                shifting(dataArea,
                         nativePlot.getDomainAxis(i),
                         nativePlot.getDomainAxisEdge(i),
                         0.0,
                         event.getPoint.getX,
                         event.getPoint.getX + offset)

            } else
              throw new Exception("Plot unsupported")

          }
        }

      }
    }
  } )

  protected def shifting(
    dataArea: Rectangle2D,
    axis: ValueAxis,
    edge: RectangleEdge,
    interval: Double,
    from: Double,
    to: Double) {

    // Calculate shift interval.
    var diffValue =
      if(interval == 0.0)
        axis.java2DToValue(from, dataArea, edge) -
        axis.java2DToValue(to, dataArea, edge)
      else
        (axis.java2DToValue(from, dataArea, edge) / interval).toInt * interval -
        (axis.java2DToValue(to, dataArea, edge) / interval).toInt * interval

    // Readjust if boundaries exceeded.
    if(axis.isInstanceOf[NumberAxis] &&
       ((axis.asInstanceOf[NumberAxis].getRangeType == RangeType.POSITIVE &&
        axis.getLowerBound > 0.0 &&
        axis.getLowerBound + diffValue < 0.0) ||
       (axis.asInstanceOf[NumberAxis].getRangeType == RangeType.NEGATIVE &&
        axis.getUpperBound < 0.0 &&
        axis.getUpperBound + diffValue > 0.0))) {

      if(axis.asInstanceOf[NumberAxis].getRangeType == RangeType.POSITIVE)
        axis.setRange(org.jfree.data.Range
          .shift(axis.getRange, 0 - axis.getLowerBound, true))
      else if(axis.asInstanceOf[NumberAxis].getRangeType == RangeType.NEGATIVE)
        axis.setRange(org.jfree.data.Range
          .shift(axis.getRange, 0 - axis.getUpperBound, true))

    // Shift.
    } else if(!axis.isInstanceOf[NumberAxis] ||
            (axis.asInstanceOf[NumberAxis].getRangeType == RangeType.FULL ||
             (axis.asInstanceOf[NumberAxis].getRangeType == RangeType.POSITIVE &&
              axis.getLowerBound + diffValue > 0.0) ||
             (axis.asInstanceOf[NumberAxis].getRangeType == RangeType.NEGATIVE &&
              axis.getUpperBound + diffValue < 0.0)))
      axis.setRange(org.jfree.data.Range.shift(axis.getRange, diffValue, true))

  }
}

