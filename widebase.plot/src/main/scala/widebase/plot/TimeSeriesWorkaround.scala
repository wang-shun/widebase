package org.jfree.data.time

/** Workaround to make `getRawDataItem` rewritable.
 *
 * @param name of series
 */
abstract class TimeSeriesWorkaround(name: String) extends TimeSeries(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    super.getRawDataItem(index)

}

