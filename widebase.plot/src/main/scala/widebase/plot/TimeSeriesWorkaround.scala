package org.jfree.data.time

abstract class TimeSeriesWorkaround(name: String) extends TimeSeries(name) {

  override def getRawDataItem(index: Int): TimeSeriesDataItem =
    super.getRawDataItem(index)

}

