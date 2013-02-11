package widebase.ui.chart.data.time

import org.jfree.data.time. { Day, TimeSeriesDataItem }

import widebase.db.column. { DateColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param period column
 * @param value column
 * @param key of series
 *
 * @author myst3r10n
 **/
class DateSeries(
  protected val period: DateColumn,
  protected val value: TypedColumn[Number],
  key: String)
  extends TimeSeriesLike(key) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val periodValue = period(index)

    new TimeSeriesDataItem(
      new Day(
        periodValue.getDayOfMonth,
        periodValue.getMonthOfYear,
        periodValue.getYear),
      value(index))

  }
}

