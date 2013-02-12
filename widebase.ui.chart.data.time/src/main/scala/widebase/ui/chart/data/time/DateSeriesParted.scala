package widebase.ui.chart.data.time

import org.jfree.data.time. { Day, TimeSeriesDataItem }

import scala.collection.mutable.ArrayBuffer

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

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val part = parts.indexWhere {
      case (min, max, record) => min <= index && index <= max }

    val record = index - parts(part)._1

    val periodValue = period(part)(record)

    new TimeSeriesDataItem(
      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      value(part)(record))

  }
}

