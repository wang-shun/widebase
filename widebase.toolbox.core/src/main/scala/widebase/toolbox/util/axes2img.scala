package widebase.toolbox.core.util

import java.awt.image.BufferedImage

import widebase.toolbox.core.graphics.impl.AxesPanel

/** Converts an axes to [[java.awt.image.BufferedImage]].
 *
 * @author myst3r10n
 */
object axes2img {

  import widebase.toolbox.core.graphics.gca

  /** Converts current axes to [[java.awt.image.BufferedImage]].
   *
   * @param width of image
   * @param height of image
   *
   * @return image of axes
   */
  def apply(width: Int, height: Int): BufferedImage = this(gca, width, height)

  /** Converts specific axes to [[java.awt.image.BufferedImage]].
   *
   * @param axes handle
   * @param width of image
   * @param height of image
   *
   * @return image of axes
   */
  def apply(axes: AxesPanel, width: Int, height: Int) =
    axes.peer.getChart.createBufferedImage(width, height)

}

