package widebase.toolbox.core.specgraph

import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.FastScatterPlot

import widebase.db.column.TypedColumn
import widebase.toolbox.core.graphics.impl.AxesPanel

/** Scatter plot.
 *
 * @author myst3r10n
 */
object scatter {

  import widebase.toolbox.core.graphics.gca

 /** plot scatter into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param x values
   * @param y values
   *
   * @return plot
   */
  def apply(x: Any, y: Any): FastScatterPlot = scatter(gca, x, y)

  /** Plot scatter into specific axes.
   *
   * @param axes handle
   * @param x values
   * @param y values
   *
   * @return plot
   */
  def apply(axes: AxesPanel, x: Any, y: Any) = {

    var data = Array.fill(2)(Array[Float]())
    val domainAxis = new NumberAxis
    val rangeAxis = new NumberAxis

    // Init xy series (column, column)
    if(x.isInstanceOf[TypedColumn[_]] &&
      y.isInstanceOf[TypedColumn[_]]) {

      x.asInstanceOf[TypedColumn[Number]].foreach(value =>
        data(0) = data.head :+ value.floatValue)

      y.asInstanceOf[TypedColumn[Number]].foreach(value =>
        data(1) = data.last :+ value.floatValue)

    // Init xy series (partitioned column, partitioned column)
    } else if(x.isInstanceOf[Array[TypedColumn[_]]] &&
      y.isInstanceOf[Array[TypedColumn[_]]]) {

      x.asInstanceOf[Array[TypedColumn[Number]]].foreach(
        _.foreach(value => data(0) = data.head :+ value.floatValue))

      y.asInstanceOf[Array[TypedColumn[Number]]].foreach(
        _.foreach(value => data(1) = data.last :+ value.floatValue))

    } else
      throw new Exception("Series type not found")

    if(!axes.peer.getChart.getPlot.isInstanceOf[FastScatterPlot])
      axes.peer.setChart(AxesPanel.default.chart(new FastScatterPlot))

    val plot = axes.peer.getChart.getPlot.asInstanceOf[FastScatterPlot]

    plot.setData(data)
    plot.setDomainAxis(domainAxis)
    plot.setRangeAxis(rangeAxis)

    plot

  }
}

