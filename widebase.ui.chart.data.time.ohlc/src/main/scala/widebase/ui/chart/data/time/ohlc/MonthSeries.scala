package widebase.ui.chart.data.time.ohlc

import org.jfree.data.ComparableObjectItem
import org.jfree.data.time.Month
import org.jfree.data.time.ohlc.OHLCItem

import widebase.db.column. { DoubleColumn, MonthColumn }

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
class MonthSeries(
  protected val period: MonthColumn,
  protected val open: DoubleColumn,
  protected val high: DoubleColumn,
  protected val low: DoubleColumn,
  protected val close: DoubleColumn,
  key: String)
  extends OHLCSeriesLike(key) {

  override def getDataItem(index: Int): ComparableObjectItem = {

    val periodValue = period(index)

    new OHLCItem(
      new Month(periodValue.getMonthOfYear, periodValue.getYear),
      open(index),
      high(index),
      low(index),
      close(index))

  }
}

