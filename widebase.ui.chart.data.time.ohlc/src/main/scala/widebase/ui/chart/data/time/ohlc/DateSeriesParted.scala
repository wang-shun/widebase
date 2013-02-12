package widebase.ui.chart.data.time.ohlc

import org.jfree.data.ComparableObjectItem
import org.jfree.data.time.Day
import org.jfree.data.time.ohlc.OHLCItem

import scala.collection.mutable.ArrayBuffer

import widebase.db.column. { DateColumn, DoubleColumn }

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
class DateSeriesParted(
  protected val period: Array[DateColumn],
  protected val open: Array[DoubleColumn],
  protected val high: Array[DoubleColumn],
  protected val low: Array[DoubleColumn],
  protected val close: Array[DoubleColumn],
  key: String)
  extends OHLCSeriesPartedLike(key) {

  protected val parts = ArrayBuffer[(Int, Int, Int)]()

  {

    var offset = 0

    for(i <- 0 to period.size - 1) {

      if(i == 0)
        parts += ((0, period(i).length - 1, 0))
      else
        parts += ((offset, offset + period(i).length - 1, i))

      offset += period(i).length

    }
  }

  protected val records = {

    var total = 0

    period.foreach(total += _.length)

    total

  }

  override def getDataItem(index: Int): ComparableObjectItem = {

    val part = parts.indexWhere {
      case (min, max, record) => min <= index && index <= max }

    val record = index - parts(part)._1

    val periodValue = period(part)(record)

    new OHLCItem(
      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      open(part)(record),
      high(part)(record),
      low(part)(record),
      close(part)(record))

  }
}

