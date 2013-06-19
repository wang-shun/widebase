package widebase.toolbox.core

import java.awt.Color
import java.util.UUID

import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.util.ShapeUtilities

import widebase.db.column.TypedColumn
import widebase.ui.chart.data. { ValueFunction, ValuePartitionFunction }
import widebase.ui.chart.data.xy. { XYPartitionSeries, XYSeries }
import widebase.toolbox.core.graphics.impl.AxesPanel
import widebase.toolbox.core.uitools.impl.Tab

/** Graph2d package.
 *
 * @author myst3r10n
 */
package object graph2d {

  import scala.util.control.Breaks. { break, breakable }

  import widebase.toolbox.core.graphics. { gca, gcf }

  /** Plot 2-D line into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param values of Xn, Yn, format or properties
   *
   * @return plot handle
   */
  def plot(values: Any*): XYPlot = plot(gca, values:_*)

  /** Plot 2-D line into specific axes.
   *
   * @param axes handle
   * @param values of Xn, Yn, format or properties
   *
   * @return plot handle
   */
  def plot(axes: AxesPanel, values: Any*) = {

    if(!axes.peer.getChart.getPlot.isInstanceOf[XYPlot])
      axes.peer.setChart(AxesPanel.default.chart(new XYPlot))

    val plot = axes.peer.getChart.getXYPlot

    var dataset = new XYSeriesCollection

    val domainAxis =
      if(axes.hold)
        plot.getDomainAxis
      else
        new NumberAxis

    val rangeAxis =
      if(axes.hold)
        plot.getRangeAxis
      else
        new NumberAxis

    val renderer = new XYLineAndShapeRenderer(true, false)

    var i = 0

    while(i < values.length) {

      // Init any xy series
      val series =
        // Init xy series (column, column)
        if(values(i).isInstanceOf[TypedColumn[_]] &&
          values(i + 1).isInstanceOf[TypedColumn[_]])
          new XYSeries(
            UUID.randomUUID.toString,
            values(i).asInstanceOf[TypedColumn[Number]],
            values(i + 1).asInstanceOf[TypedColumn[Number]])
        // Init xy series (partitioned column, partitioned column)
        else if(values(i).isInstanceOf[Array[TypedColumn[_]]] &&
          values(i + 1).isInstanceOf[Array[TypedColumn[_]]])
          new XYPartitionSeries(
            UUID.randomUUID.toString,
            values(i).asInstanceOf[Array[TypedColumn[Number]]],
            values(i + 1).asInstanceOf[Array[TypedColumn[Number]]])
        // Init xy series (column, function)
        else if(values(i).isInstanceOf[TypedColumn[_]] &&
          values(i + 1).isInstanceOf[ValueFunction])
          new XYSeries(
            UUID.randomUUID.toString,
            values(i).asInstanceOf[TypedColumn[Number]],
            values(i + 1).asInstanceOf[ValueFunction])
        // Init xy series (partitioned column, function)
        else if(values(i).isInstanceOf[Array[TypedColumn[_]]] &&
          values(i + 1).isInstanceOf[ValuePartitionFunction])
          new XYPartitionSeries(
            UUID.randomUUID.toString,
            values(i).asInstanceOf[Array[TypedColumn[Number]]],
            values(i + 1).asInstanceOf[ValuePartitionFunction])
        else
          throw new Exception("Series type not found")

      dataset.addSeries(series)

      i += 2

      // Get default color
      var color = gcf.default.axes.colorOrder(
        (dataset.getSeriesCount - 1) % gcf.default.axes.colorOrder.size)

      var fromRecord = -1
      var tillRecord = -1

      breakable {

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
              domainAxis.setLowerBound(series.getX(record).doubleValue)

            case "Till" =>

              i += 1

              val record = values(i).asInstanceOf[Int]

              tillRecord = record
              domainAxis.setUpperBound(series.getX(record).doubleValue)

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

            case _ => break

          }

          i += 1

        }
      }

      // Set color
      renderer.setSeriesPaint(
        i,
        gcf.default.axes.colorOrder(
          dataset.getSeriesCount - 1 % gcf.default.axes.colorOrder.size))

      // Disable legend by default
      renderer.setSeriesVisibleInLegend(dataset.getSeriesCount - 1, false)

      if(!axes.hold) {

        // Fix lower bound if only upper bound was set
        if(fromRecord == -1 && tillRecord != -1)
          domainAxis.setLowerBound(series.getX(0).doubleValue)

        // Fix upper bound if only lower bound was set
        if(fromRecord != -1 && tillRecord == -1)
          domainAxis.setUpperBound(series.getX(series.getItemCount - 1).doubleValue)

      }
    }

    if(axes.hold) {

      plot.setDataset(plot.getDatasetCount, dataset)
      plot.setRenderer(plot.getRendererCount, renderer)

    } else {

      plot.setDataset(0, dataset)
      plot.setDomainAxis(0, domainAxis)
      plot.setRangeAxis(0, rangeAxis)
      plot.setRenderer(0, renderer)

    }

    plot

  }
}

