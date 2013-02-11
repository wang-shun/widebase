package widebase.ui.chart.data.time

import org.jfree.data.time. { TimeSeriesDataItem, TimeSeriesWorkaround }

import widebase.db.column.TypedColumn

/** A common class for inherited `TimeSeries`.
 *
 * @param key of series
 *
 * @author myst3r10n
 **/
abstract class TimeSeriesLike(key: String) extends TimeSeriesWorkaround(key) {

  protected val period: TypedColumn[_]

  override def getItemCount = period.length

}

