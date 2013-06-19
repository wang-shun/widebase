package widebase.toolbox.core.graphics.impl

import scala.collection.mutable.ArrayBuffer
import scala.swing.Color

/** Some default properties.
 *
 * @author myst3r10n
 */
trait DefaultProperties {

  /** Default properties of [[widebase.toolbox.core.graphics.impl.AxesPanel]].
   *
   * @author myst3r10n
   */
  object axes {

    /** The `DefaultAxesColorOrder` property. */
    val colorOrder = ArrayBuffer[Color](
      new Color(0.0f, 0.0f, 1.0f),
      new Color(0.0f, 0.5f, 0.0f),
      new Color(1.0f, 0.0f, 0.0f),
      new Color(0.0f, 0.75f, 0.75f),
      new Color(0.75f, 0.0f, 0.75f),
      new Color(0.75f, 0.75f, 0.0f),
      new Color(0.25f, 0.25f, 0.25f))

  }
}

