package widebase.ui.chart.data

/** A table file and directory table compatible function.
 *
 * @param function call
 *
 * @author myst3r10n
 **/
case class ValueFunction(function: (Int) => Number) {

  def apply(record: Int) = function(record)

}

