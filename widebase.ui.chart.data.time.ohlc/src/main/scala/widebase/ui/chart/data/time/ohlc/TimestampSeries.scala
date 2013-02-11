package widebase.ui.chart.data.time.ohlc

import org.jfree.data.ComparableObjectItem
import org.jfree.data.time.Millisecond
import org.jfree.data.time.ohlc.OHLCItem

import widebase.db.column. { DoubleColumn, TimestampColumn }

/** A table file and directory table compatible `OHLCSeries`.
 *
 * @param period column
 * @param open column
 * @param high column
 * @param low column
 * @param close column
 * @param key of series
 *
 * @author myst3r10n
 **/
class TimestampSeries(
  protected val period: TimestampColumn,
  protected val open: DoubleColumn,
  protected val high: DoubleColumn,
  protected val low: DoubleColumn,
  protected val close: DoubleColumn,
  key: String)
  extends OHLCSeriesLike(key) {

  override def getDataItem(index: Int): ComparableObjectItem =
    new OHLCItem(
      new Millisecond(period(index)),
      open(index),
      high(index),
      low(index),
      close(index))

}

