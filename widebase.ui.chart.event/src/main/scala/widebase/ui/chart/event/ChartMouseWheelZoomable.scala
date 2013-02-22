package widebase.ui.chart.event

import scala.swing.event.Event

/** Zoomable chart by mouse wheel.
 *
 * @param enabled zooming
 *
 * @author myst3r10n
 */
case class ChartMouseWheelZoomable(val enabled: Boolean) extends Event

