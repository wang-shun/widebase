package widebase.ui.chart.data.time.ohlc

import org.jfree.data.ComparableObjectItem
import org.jfree.data.time.Millisecond
import org.jfree.data.time.ohlc.OHLCItem

import widebase.db.column. { DoubleColumn, TimestampColumn }

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
class TimestampSeriesParted(
  protected val period: Array[TimestampColumn],
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

    val part = (index / period.head.length).toInt
    val record = index % period.head.length

    new OHLCItem(
      new Millisecond(period(part)(record)),
      open(part)(record),
      high(part)(record),
      low(part)(record),
      close(part)(record))

  }
}

