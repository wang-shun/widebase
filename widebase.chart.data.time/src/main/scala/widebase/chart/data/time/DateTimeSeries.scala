package widebase.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { DateTimeColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param events column
 * @param values column
 * @param name of series
 *
 * @author myst3r10n
 **/
class DateTimeSeries(
  protected val events: DateTimeColumn,
  protected val values: TypedColumn[Number],
  name: String)
  extends TimeSeriesLike(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val event = events(index)

    new TimeSeriesDataItem(
      new Millisecond(
        event.getMillisOfSecond,
        event.getSecondOfMinute,
        event.getMinuteOfHour,
        event.getHourOfDay,
        event.getDayOfMonth,
        event.getMonthOfYear,
        event.getYear),
      values(index))

  }
}
