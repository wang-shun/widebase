package widebase.toolbox.core.graph2d

import org.jfree.chart.plot.XYPlot

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Label y-axis.
 *
 * @author myst3r10n
 */
object ylabel {

  import widebase.toolbox.core.graphics.gca

  /** Set label of y-axis.
   *
   * @param label of y-axis
   *
   * @return label handle
   */
  def ylabel(label: String): String = ylabel(gca, label)

  /** Set label of y-axis.
   *
   * @param axes handle
   * @param label of y-axis
   *
   * @return label handle
   */
  def ylabel(axes: AxesPanel, label: String) = {

    axes.peer.getChart.getPlot match {

      case plot: XYPlot =>

        plot.getRangeAxis.setLabel(label)
        plot.getRangeAxis.getLabel

      case plot => throw new Exception("Plot not supported: " + plot.toString)

    }
  }
}

