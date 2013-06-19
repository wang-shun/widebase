package widebase.toolbox.core.graph2d

import org.jfree.chart.plot.XYPlot

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Label x-axis.
 *
 * @author myst3r10n
 */
object xlabel {

  import widebase.toolbox.core.graphics.gca

  /** Set label of x-axis.
   *
   * @param label of x-axis
   *
   * @return label handle
   */
  def xlabel(label: String): String = xlabel(gca, label)

  /** Set label of x-axis.
   *
   * @param axes handle
   * @param label of x-axis
   *
   * @return label handle
   */
  def xlabel(axes: AxesPanel, label: String) = {

    axes.peer.getChart.getPlot match {

      case plot: XYPlot =>

        plot.getDomainAxis.setLabel(label)
        plot.getDomainAxis.getLabel

      case plot => throw new Exception("Plot not supported: " + plot.toString)

    }
  }
}

