package org.jfree.data.time

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param name of series
 *
 * @author myst3r10n
 */
abstract class TimeSeriesWorkaround(name: String) extends TimeSeries(name) {

  override def getDataItem(index: Int): TimeSeriesDataItem = getRawDataItem(index)

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    super.getRawDataItem(index)

}

