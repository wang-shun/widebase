package widebase.ui.chart.plot

import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.FastScatterPlot

import scala.util.control.Breaks. { break, breakable }

import widebase.db.column.TypedColumn

/** Scatter.
 *
 * @author myst3r10n
 */
object Scatter {

  /** Perform plot.
   *
   * @param values of data, properties and format
   *
   * @return plot
   */
  def apply(values: Any*) = {

    var data = Array.fill(2)(Array[Float]())
    val domainAxis = new NumberAxis
    val rangeAxis = new NumberAxis

    var i = 0

    while(i < values.length) {

      // Init xy series (column, column)
      if(values(i).isInstanceOf[TypedColumn[_]] &&
        values(i + 1).isInstanceOf[TypedColumn[_]]) {

        values(i).asInstanceOf[TypedColumn[Number]].foreach(value =>
          data(0) = data.head :+ value.floatValue)

        values(i + 1).asInstanceOf[TypedColumn[Number]].foreach(value =>
          data(1) = data.last :+ value.floatValue)

      // Init xy series (partitioned column, partitioned column)
      } else if(values(i).isInstanceOf[Array[TypedColumn[_]]] &&
        values(i + 1).isInstanceOf[Array[TypedColumn[_]]]) {

        values(i).asInstanceOf[Array[TypedColumn[Number]]].foreach(
          _.foreach(value => data(0) = data.head :+ value.floatValue))

        values(i + 1).asInstanceOf[Array[TypedColumn[Number]]].foreach(
          _.foreach(value => data(1) = data.last :+ value.floatValue))

      } else
        throw new Exception("Series type not found")

      i += 2

    }

    new FastScatterPlot(data, domainAxis, rangeAxis)

  }
}

