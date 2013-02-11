package widebase.ui.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { DateTimeColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param period column
 * @param value column
 * @param key of series
 *
 * @author myst3r10n
 **/
class DateTimeSeries(
  protected val period: DateTimeColumn,
  protected val value: TypedColumn[Number],
  key: String)
  extends TimeSeriesLike(key) {

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
      value(index))

  }
}
