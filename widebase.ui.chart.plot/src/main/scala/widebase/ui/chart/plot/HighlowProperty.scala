package widebase.ui.chart.plot

import org.jfree.chart.axis.DateAxis
import org.jfree.data.time.ohlc.OHLCSeries

/** Highlow properties.
 *
 * @author myst3r10n
 */
object HighlowProperty {

  /** Perform a property.
   *
   * @param series of collection
   * @param domainAxis of plotter
   * @param property name
   * @param value of property
   **/
  def apply(
    series: OHLCSeries,
    domainAxis: DateAxis,
    property: String,
    value: Any) {

    property match {

      case "from" => domainAxis.setLowerBound(series.getPeriod(
        value.asInstanceOf[Int]).getStart.getTime)

      case "till" => domainAxis.setUpperBound(series.getPeriod(
        value.asInstanceOf[Int]).getStart.getTime)

      case _ => throw new Exception("Property not found: " + property)

    }
  }
}

