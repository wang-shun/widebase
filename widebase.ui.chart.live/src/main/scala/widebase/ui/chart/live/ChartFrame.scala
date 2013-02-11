package widebase.ui.chart.live

import org.jfree.chart.ChartPanel

import scala.swing.Publisher

/** Chart's frame.
 * 
 * @param panel0 of chart
 * @param width of frame
 * @param height of frame
 * @param figure number
 *
 * @author myst3r10n
 */
case class ChartFrame(
  panel0: ChartPanel with Publisher,
  width: Int = 800,
  height: Int = 600,
  val figure: Int)
  extends widebase.ui.chart.ChartFrame(panel0, width, height)

