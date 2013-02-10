package widebase.chart.data.time

import org.jfree.data.time. { Day, TimeSeriesDataItem }

import widebase.db.column. { DateColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param events column
 * @param values column
 * @param name of series
 *
 * @author myst3r10n
 **/
class DateSeries(
  protected val events: DateColumn,
  protected val values: TypedColumn[Number],
  name: String)
  extends TimeSeriesLike(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val event = events(index)

    new TimeSeriesDataItem(
      new Day(event.getDayOfMonth, event.getMonthOfYear, event.getYear),
      values(index))

  }
}

