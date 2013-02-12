package widebase.ui.chart.plot

import org.jfree.chart.axis.ValueAxis
import org.jfree.data.general.Series
import org.jfree.data.time.TimeSeriesWorkaround

import widebase.ui.chart.data.xy.XYSeries

/** Plot properties.
 *
 * @author myst3r10n
 */
object PlotProperty {

  /** Perform a property.
   *
   * @param series of collection
   * @param domainAxis of plotter
   * @param property name
   * @param value of property
   **/
  def apply(
    series: Series,
    domainAxis: ValueAxis,
    property: String,
    value: Any) {

    property match {

      case "from" =>

        val fromRecord = value.asInstanceOf[Int]

        if(series.isInstanceOf[TimeSeriesWorkaround])
          domainAxis.setLowerBound(
            series.asInstanceOf[TimeSeriesWorkaround]
              .getTimePeriod(fromRecord).getStart.getTime)
        else
          domainAxis.setLowerBound(
            series.asInstanceOf[XYSeries].getX(fromRecord).doubleValue)

      case "till" =>

        val tillRecord = value.asInstanceOf[Int]

        if(series.isInstanceOf[TimeSeriesWorkaround])
          domainAxis.setUpperBound(series.asInstanceOf[TimeSeriesWorkaround]
            .getTimePeriod(tillRecord).getStart.getTime)
        else
          domainAxis.setUpperBound(
            series.asInstanceOf[XYSeries].getX(tillRecord).doubleValue)

      case _ => throw new Exception("Property not found: " + property)

    }
  }
}

