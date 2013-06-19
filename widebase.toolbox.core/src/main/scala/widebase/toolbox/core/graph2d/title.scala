package widebase.toolbox.core.graph2d

import org.jfree.chart.title.TextTitle

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Set title.
 *
 * @author myst3r10n
 */
object title {

  import widebase.toolbox.core.graphics.gca

  /** Set title of current axes.
   *
   * @param text of title
   *
   * @return title handle
   */
  def apply(text: String): TextTitle = title(gca, text)

  /** Set title of specific axes.
   *
   * @param axes handle
   * @param text of title
   *
   * @return title handle
   */
  def apply(axes: AxesPanel, text: String) = {

    axes.peer.getChart.setTitle(text)
    axes.peer.getChart.getTitle

  }
}

