package widebase.ui.chart.data.time

import org.jfree.data.time. { TimeSeriesDataItem, TimeSeriesWorkaround }

import widebase.db.column.TypedColumn
import widebase.ui.chart.data.ValuePartitionFunction

/** A common class for inherited `TimeSeries`.
 *
 * @param name of series
 *
 * @author myst3r10n
 **/
abstract class TimePartitionSeriesLike(
  name: String)
  extends TimeSeriesWorkaround(name) {

  protected val records: Int

  override def getItemCount = records

}

