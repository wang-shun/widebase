package widebase.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { DateTimeColumn, TypedColumn }

/** A partitioned table compatible `TimeSeries`.
 *
 * @param events series of event columns
 * @param values series of value columns
 * @param name of series
 *
 * @author myst3r10n
 **/
class DateTimeSeriesParted(
  protected val events: Array[DateTimeColumn],
  protected val values: Array[TypedColumn[Number]],
  name: String)
  extends TimeSeriesPartedLike(name) {

  protected val records = {

    var total = 0

    events.foreach(total += _.length)

    total

  }

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    var part = (index / events.head.length).toInt
    var record = index % events.head.length

    if(record >= events(part).length) {

      // Fix BufferUnderflowException.
      // Because not all tables have same record length.
      // In that case we increment one partition and reset index

      part += 1
      record = 0

    }

    val event = events(part)(record)

    new TimeSeriesDataItem(
      new Millisecond(
        event.getMillisOfSecond,
        event.getSecondOfMinute,
        event.getMinuteOfHour,
        event.getHourOfDay,
        event.getDayOfMonth,
        event.getMonthOfYear,
        event.getYear),
      values(part)(record))

  }
}

