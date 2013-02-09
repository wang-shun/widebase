package org.jfree.data.xy

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param name of series
 *
 * @author myst3r10n
 */
abstract class XYSeriesWorkaround(name: String) extends XYSeries(name) {

  override def getDataItem(index: Int): XYDataItem =
    getRawDataItem(index)

  override def getRawDataItem(index: Int): XYDataItem =
    super.getRawDataItem(index)

}

