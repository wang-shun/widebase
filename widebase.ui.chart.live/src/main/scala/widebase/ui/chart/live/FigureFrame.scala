package widebase.ui.chart.live

import org.jfree.chart.ChartPanel

import scala.swing.Publisher

import widebase.ui.chart.ChartFrame

/** Frame of chart.
 * 
 * @param panel1 of chart
 * @param width of frame
 * @param height of frame
 * @param figure number
 *
 * @author myst3r10n
 */
case class FigureFrame(
  panel1: ChartPanel with Publisher,
  width: Int = 800,
  height: Int = 600,
  val figure: Int)
  extends ChartFrame(panel1, width, height)

