package widebase.ui.chart.data.time

import org.jfree.data.time. { Day, TimeSeriesDataItem }

import widebase.db.column. { DateColumn, TypedColumn }

/** A partitioned table compatible `TimeSeries`.
 *
 * @param period series of period columns
 * @param value series of value columns
 * @param key of series
 *
 * @author myst3r10n
 **/
class DateSeriesParted(
  protected val period: Array[DateColumn],
  protected val value: Array[TypedColumn[Number]],
  key: String)
  extends TimeSeriesPartedLike(key) {

  protected val records = {

    var total = 0

    period.foreach(total += _.length)

    total

  }

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    var part = (index / period.head.length).toInt
    var record = index % period.head.length

    if(record >= period(part).length) {

      // Fix BufferUnderflowException.
      // Because not all tables have same record length.
      // In that case we increment one partition and reset index

      part += 1
      record = 0

    }

    val periodValue = period(part)(record)

    new TimeSeriesDataItem(
      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      value(part)(record))

  }
}

