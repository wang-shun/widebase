package widebase.toolbox.core.scribe

import org.jfree.chart. { LegendItem, LegendItemCollection }
import org.jfree.chart.plot.XYPlot

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Legend of graph.
 *
 * @author myst3r10n
 */
object legend {

  import widebase.toolbox.core.graphics.gca

  /** Show legend of current axes.
   * If no axes or figure exists, creates one.
   *
   * @param labels of legend
   *
   * @return legend handle
   */
  def apply(labels: String*): LegendItemCollection = legend(gca, labels:_*)

  /** Show legend of specific axes.
   *
   * @param axes handle
   * @param labels of legend
   *
   * @return legend handle
   */
  def apply(axes: AxesPanel, labels: String*) = {

    val replacedItems = new LegendItemCollection

    axes.peer.getChart.getPlot match {

      case plot: XYPlot =>

        for(i <- 0 to plot.getDatasetCount - 1)
          for(j <- 0 to plot.getDataset(i).getSeriesCount - 1)
            plot.getRenderer(i).setSeriesVisibleInLegend(j, true)

        plot.setFixedLegendItems(null)
        val currentItems = plot.getLegendItems

        try {

          for(i <- 0 to labels.size - 1) {

            val item = currentItems.get(i)

            replacedItems.add(new LegendItem(
              labels(i),
              item.getDescription,
              item.getToolTipText,
              item.getURLText,
              item.isShapeVisible,
              item.getShape,
              item.isShapeFilled,
              item.getFillPaint,
              item.isShapeOutlineVisible,
              item.getOutlinePaint,
              item.getOutlineStroke,
              item.isLineVisible,
              item.getLine,
              item.getLineStroke,
              item.getLinePaint))

          }
        } finally {

          // Replace legend also on exception!
          plot.setFixedLegendItems(replacedItems)

        }

      case plot => throw new Exception("Plot not supported: " + plot.toString)

    }

    replacedItems

  }
}

