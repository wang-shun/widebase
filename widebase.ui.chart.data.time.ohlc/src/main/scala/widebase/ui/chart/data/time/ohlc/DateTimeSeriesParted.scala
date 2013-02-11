package widebase.ui.chart.data.time.ohlc

import org.jfree.data.ComparableObjectItem
import org.jfree.data.time.Millisecond
import org.jfree.data.time.ohlc.OHLCItem

import widebase.db.column. { DateTimeColumn, DoubleColumn }

/** A partitioned table compatible `OHLCSeries`.
 *
 * @param period series of period columns
 * @param open series of open columns
 * @param high series of high columns
 * @param low series of low columns
 * @param close series of close columns
 * @param key of series
 *
 * @author myst3r10n
 **/
class DateTimeSeriesParted(
  protected val period: Array[DateTimeColumn],
  protected val open: Array[DoubleColumn],
  protected val high: Array[DoubleColumn],
  protected val low: Array[DoubleColumn],
  protected val close: Array[DoubleColumn],
  key: String)
  extends OHLCSeriesPartedLike(key) {

  protected val records = {

    var total = 0

    period.foreach(total += _.length)

    total

  }

  override def getDataItem(index: Int): ComparableObjectItem = {

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

    new OHLCItem(
      new Millisecond(
        periodValue.getMillisOfSecond,
        periodValue.getSecondOfMinute,
        periodValue.getMinuteOfHour,
        periodValue.getHourOfDay,
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      open(part)(record),
      high(part)(record),
      low(part)(record),
      close(part)(record))

  }
}

