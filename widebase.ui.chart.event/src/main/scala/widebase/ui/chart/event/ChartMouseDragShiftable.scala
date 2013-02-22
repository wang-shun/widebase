package widebase.ui.chart.event

import scala.swing.event.Event

/** Shiftable chart by mouse drag.
 *
 * @param enabled shiftable
 *
 * @author myst3r10n
 */
case class ChartMouseDragShiftable(val enabled: Boolean) extends Event

