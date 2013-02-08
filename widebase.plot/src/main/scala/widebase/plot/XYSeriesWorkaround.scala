package org.jfree.data.xy

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param name of series
 */
abstract class XYSeriesWorkaround(name: String) extends XYSeries(name) {

  override def getRawDataItem(index: Int): XYDataItem =
    super.getRawDataItem(index)

}

