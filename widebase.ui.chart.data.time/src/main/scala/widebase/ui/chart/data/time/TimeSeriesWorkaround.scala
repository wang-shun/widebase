package org.jfree.data.time

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param key of series
 *
 * @author myst3r10n
 */
abstract class TimeSeriesWorkaround(key: String) extends TimeSeries(key) {

  override def getDataItem(index: Int): TimeSeriesDataItem = getRawDataItem(index)

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    super.getRawDataItem(index)

}

