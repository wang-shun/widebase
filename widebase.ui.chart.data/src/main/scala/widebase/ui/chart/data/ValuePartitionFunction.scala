package widebase.ui.chart.data

/** A partitioned table compatible function.
 *
 * @param function call
 *
 * @author myst3r10n
 **/
case class ValuePartitionFunction(function: (Int, Int) => Number) {

  def apply(part: Int, record: Int) = function(part, record)

}

