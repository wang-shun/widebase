package widebase.plot.data.time

import org.jfree.data.time. { Month, TimeSeriesDataItem }

import widebase.db.column. { MonthColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param events column
 * @param values column
 * @param name of series
 *
 * @author myst3r10n
 **/
class MonthSeries(
  protected val events: MonthColumn,
  protected val values: TypedColumn[Number],
  name: String)
  extends TimeSeriesLike(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val event = events(index)

    new TimeSeriesDataItem(
      new Month(event.getMonthOfYear, event.getYear),
      values(index))

  }
}

