package widebase.ui.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { TimestampColumn, TypedColumn }
import widebase.ui.chart.data.ValueFunction

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param name of series
 * @param period column
 * @param value column
 * @param function call
 *
 * @author myst3r10n
 **/
class TimestampSeries(
  name: String,
  protected val period: TimestampColumn,
  protected val value: TypedColumn[Number],
  function: ValueFunction = null)
  extends TimeSeriesLike(name) {

  def this(
    name: String,
    period: TimestampColumn,
    function: ValueFunction) =
    this(name, period, null, function)

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    new TimeSeriesDataItem(
      new Millisecond(period(index)),
      if(value == null) function(index) else value(index))

}

