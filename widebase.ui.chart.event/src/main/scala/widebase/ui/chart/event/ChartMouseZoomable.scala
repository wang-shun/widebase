package widebase.ui.chart.event

import scala.swing.event.Event

/** Zoomable chart by mouse selection.
 *
 * @param enabled zooming
 *
 * @author myst3r10n
 */
case class ChartMouseZoomable(val enabled: Boolean) extends Event

