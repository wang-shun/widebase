package widebase.toolbox.core.graphics

import impl.AxesPanel

/** Manage behavior of axes.
 *
 * @author myst3r10n
 */
object hold {

  /** Reverse hold state of current axes.
   * If no axes or figure exists, creates one.
   */
  def apply() { gca.hold = !gca.hold }

  /** Reverse hold state of an axes.
   *
   * @param axes handle
   */
  def apply(axes: AxesPanel) { axes.hold = !axes.hold }

  /** Turn hold on of current axes.
   * If no axes or figure exists, creates one.
   */
  def on { gca.hold = true }

  /** Turn hold off of current axes.
   * If no axes or figure exists, creates one.
   */
  def off { gca.hold = false }

}

