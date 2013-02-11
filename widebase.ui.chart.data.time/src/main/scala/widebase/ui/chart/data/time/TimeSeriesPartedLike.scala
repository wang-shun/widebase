package widebase.ui.chart.data.time

import org.jfree.data.time. { TimeSeriesDataItem, TimeSeriesWorkaround }

import widebase.db.column.TypedColumn

/** A common class for inherited `TimeSeries`.
 *
 * @param key of series
 *
 * @author myst3r10n
 **/
abstract class TimeSeriesPartedLike(key: String) extends TimeSeriesWorkaround(key) {

  protected val records: Int

  override def getItemCount = records

}

