package org.jfree.data.xy

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param key of series
 *
 * @author myst3r10n
 */
abstract class XYSeriesWorkaround(key: String) extends XYSeries(key) {

  override def getDataItem(index: Int): XYDataItem =
    getRawDataItem(index)

  override def getRawDataItem(index: Int): XYDataItem =
    super.getRawDataItem(index)

}

