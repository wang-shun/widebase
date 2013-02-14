package widebase.ui.chart.data.time

import org.jfree.data.time. { Day, TimeSeriesDataItem }

import widebase.db.column. { DateColumn, TypedColumn }
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
class DateSeries(
  name: String,
  protected val period: DateColumn,
  protected val value: TypedColumn[Number],
  function: ValueFunction = null)
  extends TimeSeriesLike(name) {

  def this(
    name: String,
    period: DateColumn,
    function: ValueFunction) =
    this(name, period, null, function)

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val periodValue = period(index)

    new TimeSeriesDataItem(
      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      if(value == null) function(index) else value(index))

  }
}

