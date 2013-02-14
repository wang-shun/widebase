package widebase.ui.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { DateTimeColumn, TypedColumn }
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
class DateTimeSeries(
  name: String,
  protected val period: DateTimeColumn,
  protected val value: TypedColumn[Number],
  function: ValueFunction = null)
  extends TimeSeriesLike(name) {

  def this(
    name: String,
    period: DateTimeColumn,
    function: ValueFunction) =
    this(name, period, null, function)

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val periodValue = period(index)

    new TimeSeriesDataItem(
      new Millisecond(
        periodValue.getMillisOfSecond,
        periodValue.getSecondOfMinute,
        periodValue.getMinuteOfHour,
        periodValue.getHourOfDay,
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      if(value == null) function(index) else value(index))

  }
}
