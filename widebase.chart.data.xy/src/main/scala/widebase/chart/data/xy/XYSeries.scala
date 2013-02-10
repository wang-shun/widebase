package widebase.chart.data.xy

import org.jfree.data.xy. { XYDataItem, XYSeriesWorkaround }

import widebase.db.column.TypedColumn

/** A table file and directory table compatible `XYSeries`.
 *
 * @param x column
 * @param y column
 * @param name of series
 *
 * @author myst3r10n
 **/
class XYSeries(
  protected val x: TypedColumn[Number],
  protected val y: TypedColumn[Number],
  name: String)
  extends XYSeriesWorkaround(name) {

  override def getItemCount = x.length

  override def getRawDataItem(index: Int): XYDataItem =
    new XYDataItem(x(index), y(index))

}

