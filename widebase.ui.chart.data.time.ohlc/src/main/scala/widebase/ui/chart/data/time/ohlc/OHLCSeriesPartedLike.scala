package widebase.ui.chart.data.time.ohlc

import org.jfree.data.time.ohlc.OHLCSeries

/** A common class for inherited `OHLCSeries`.
 *
 * @param key of series
 *
 * @author myst3r10n
 **/
abstract class OHLCSeriesPartedLike(key: String) extends OHLCSeries(key) {

  protected val records: Int

  override def getItemCount = records

}

