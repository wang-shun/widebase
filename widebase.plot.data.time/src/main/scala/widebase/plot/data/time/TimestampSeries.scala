package widebase.plot.data.time

import org.jfree.data.time. { Millisecond, TimeSeriesDataItem }

import widebase.db.column. { TimestampColumn, TypedColumn }

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param events column
 * @param values column
 * @param name of series
 *
 * @author myst3r10n
 **/
class TimestampSeries(
  protected val events: TimestampColumn,
  protected val values: TypedColumn[Number],
  name: String)
  extends TimeSeriesLike(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    new TimeSeriesDataItem(new Millisecond(events(index)), values(index))

}

