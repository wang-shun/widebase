package widebase.ui.chart.data.xy

import org.jfree.data.xy. { XYDataItem, XYSeriesWorkaround }

import scala.collection.mutable.ArrayBuffer

import widebase.db.column.TypedColumn
import widebase.ui.chart.data.ValuePartitionFunction

/** A partitioned table compatible `XYSeries`.
 *
 * @param key of series
 * @param x columns
 * @param y columns
 * @param function call
 *
 * @author myst3r10n
 **/
class XYPartitionSeries(
  key: String,
  protected val x: Array[TypedColumn[Number]],
  protected val y: Array[TypedColumn[Number]],
  function: ValuePartitionFunction = null)
  extends XYSeriesWorkaround(key) {

  def this(
    key: String,
    x: Array[TypedColumn[Number]],
    function: ValuePartitionFunction) =
    this(key, x, null, function)

  protected val parts = ArrayBuffer[(Int, Int, Int)]()

  {

    var offset = 0

    for(i <- 0 to x.size - 1) {

      if(i == 0)
        parts += ((0, x(i).length - 1, 0))
      else
        parts += ((offset, offset + x(i).length - 1, i))

      offset += x(i).length

    }
  }

  override def getItemCount = {

    var records = 0

    x.foreach(records += _.length)

    records

  }

  override def getRawDataItem(index: Int): XYDataItem = {

    val part = parts.indexWhere {
      case (min, max, record) => min <= index && index <= max }

    val record = index - parts(part)._1

    new XYDataItem(
      x(part)(record),
      y(part)(record))

  }
}

