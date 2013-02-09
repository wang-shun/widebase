package widebase.plot.data.time

import org.jfree.data.time. { Month, TimeSeriesDataItem }

import widebase.db.column. { MonthColumn, TypedColumn }

/** A partitioned table compatible `TimeSeries`.
 *
 * @param events series of event columns
 * @param values series of value columns
 * @param name of series
 *
 * @author myst3r10n
 **/
class MonthSeriesParted(
  protected val events: Array[MonthColumn],
  protected val values: Array[TypedColumn[Number]],
  name: String)
  extends TimeSeriesPartedLike(name) {

  protected val records = {

    var total = 0

    events.foreach(total += _.length)

    total

  }

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val part = (index / events.head.length).toInt
    val record = index % events.head.length

    val event = events(part)(record)

    new TimeSeriesDataItem(
      new Month(event.getMonthOfYear, event.getYear),
      values(part)(record))

  }
}

