package widebase.ui.chart.data.xy

import org.jfree.data.xy. { XYDataItem, XYSeriesWorkaround }

import widebase.db.column.TypedColumn
import widebase.ui.chart.data.ValueFunction

/** A table file and directory table compatible `XYSeries`.
 *
 * @param key of series
 * @param x column
 * @param y column
 * @param function call
 *
 * @author myst3r10n
 **/
class XYSeries(
  key: String,
  protected val x: TypedColumn[Number],
  protected val y: TypedColumn[Number],
  function: ValueFunction = null)
  extends XYSeriesWorkaround(key) {

  def this(
    key: String,
    x: TypedColumn[Number],
    function: ValueFunction) =
    this(key, x, null, function)

  override def getItemCount = x.length

  override def getRawDataItem(index: Int): XYDataItem =
    new XYDataItem(x(index), if(y == null) function(index) else y(index))

}

