package widebase.plot

import org.jfree.data.time. {

  Millisecond,
  TimeSeriesDataItem,
  TimeSeriesWorkaround

}

import org.joda.time.LocalDateTime

import widebase.db.column.TypedColumn

/** A partitioned table compatible `TimeSeries`.
 *
 * @param events columns
 * @param values columns
 * @param name of series
 **/
class TimeSeriesParted(
  protected val events: Array[TypedColumn[LocalDateTime]],
  protected val values: Array[TypedColumn[Double]],
  name: String)
  extends TimeSeriesWorkaround(name) {

  override def getDataItem(index: Int): TimeSeriesDataItem =
    getRawDataItem(index)

  override def getItemCount = {

    var records = 0

    events.foreach(records += _.length)

    records

  }

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val part = (index / events.head.length).toInt
    val record = index % events.head.length

    val event = events(part)(record)
    val value = values(part)(record)

    new org.jfree.data.time.TimeSeriesDataItem(
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

