package widebase.ui.chart.event

import scala.swing.event.Event

/** Zoom by mouse.
 *
 * @author myst3r10n
 */
case class ChartZoomMouse(val enabled: Boolean) extends Event

