package widebase.ui.chart.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { TimestampColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param period column
 * @param value column
 * @param key of series
 *
 * @author myst3r10n
 **/
class TimestampSeries(
  protected val period: TimestampColumn,
  protected val value: TypedColumn[Number],
  key: String)
  extends TimeSeriesLike(key) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    new TimeSeriesDataItem(new Millisecond(period(index)), value(index))

}

