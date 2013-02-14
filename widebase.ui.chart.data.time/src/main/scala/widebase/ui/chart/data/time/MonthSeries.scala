package widebase.ui.chart.data.time

import org.jfree.data.time. { Month, TimeSeriesDataItem }

import widebase.db.column. { MonthColumn, TypedColumn }
import widebase.ui.chart.data.ValueFunction

/** A table file and directory table compatible `TimeSeries`.
 *
 * @param name of series
 * @param period column
 * @param value column
 * @param function call
 *
 * @author myst3r10n
 **/
class MonthSeries(
  name: String,
  protected val period: MonthColumn,
  protected val value: TypedColumn[Number],
  function: ValueFunction = null)
  extends TimeSeriesLike(name) {

  def this(
    name: String,
    period: MonthColumn,
    function: ValueFunction) =
    this(name, period, null, function)

  override def getRawDataItem(index: Int): TimeSeriesDataItem = {

    val periodValue = period(index)

    new TimeSeriesDataItem(
      new Month(periodValue.getMonthOfYear, periodValue.getYear),
      if(value == null) function(index) else value(index))

  }
}

