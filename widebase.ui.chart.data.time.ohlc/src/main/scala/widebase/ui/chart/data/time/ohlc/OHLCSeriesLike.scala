package widebase.ui.chart.data.time.ohlc

import org.jfree.data.time.ohlc.OHLCSeries

import widebase.db.column.TypedColumn

/** A common class for inherited `OHLCSeries`.
 *
 * @param key of series
 *
 * @author myst3r10n
 **/
abstract class OHLCSeriesLike(key: String) extends OHLCSeries(key) {

  protected val period: TypedColumn[_]

  override def getItemCount = period.length

}

