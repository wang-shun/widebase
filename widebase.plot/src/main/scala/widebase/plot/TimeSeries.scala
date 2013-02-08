package widebase.plot

import org.jfree.data.time. {

  Millisecond,
  TimeSeriesDataItem,
  TimeSeriesWorkaround

}

import org.joda.time.LocalDateTime

import widebase.db.column.TypedColumn

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param events column
 * @param values column
 * @param name of series
 **/
class TimeSeries(
  protected val events: TypedColumn[LocalDateTime],
  protected val values: TypedColumn[Double],
  name: String)
  extends TimeSeriesWorkaround(name) {

  override def getDataItem(index: Int): TimeSeriesDataItem =
    getRawDataItem(index)

  override def getItemCount = events.length

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val event = events(index)
    val value = values(index)

    new TimeSeriesDataItem(
      new Millisecond(
        event.getMillisOfSecond,
        event.getSecondOfMinute,
        event.getMinuteOfHour,
        event.getHourOfDay,
        event.getDayOfMonth,
        event.getMonthOfYear,
        event.getYear),
      value)

  }
}

