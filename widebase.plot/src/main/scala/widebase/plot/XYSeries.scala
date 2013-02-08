package widebase.plot

import org.jfree.data.xy. { XYDataItem, XYSeriesWorkaround }

import widebase.db.column.TypedColumn

/** A table file and directory table compatible `XYSeries`.
 *
 * @param x column
 * @param y column
 * @param name of series
 **/
class XYSeries(
  protected val x: TypedColumn[Double],
  protected val y: TypedColumn[Double],
  name: String)
  extends XYSeriesWorkaround(name) {

  override def getDataItem(index: Int): XYDataItem =
    getRawDataItem(index)

  override def getItemCount = x.length

  override def getRawDataItem(index: Int): XYDataItem =
    new XYDataItem(x(index), y(index))

}

