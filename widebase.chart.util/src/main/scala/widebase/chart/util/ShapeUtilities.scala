package widebase.chart.util

import java.awt.geom. { Ellipse2D, GeneralPath, Point2D }

/** Shape utilities.
 *
 * @author myst3r10n
 */
object ShapeUtilities {

  /** Draw circle.
   *
   * @param radius of circle
   *
   * @return dot
   */
  def createCircle(radius: Float) =
    new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2)

  /** Draw diagonal cross.
   *
   * @param length of cross
   * @param path of another
   *
   * @return path
   */
  def createDiagonalCross(length: Float, path: GeneralPath = new GeneralPath) = {

    path.moveTo(-length, -length)
    path.lineTo(length, length)
    path.closePath

    path.moveTo(length, -length)
    path.lineTo(-length, length)
    path.closePath

    path

  }

  /** Draw dot.
   *
   * @param radius of dot
   *
   * @return dot
   */
  def createDot(radius: Float) =
    new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2)

  /** Draw regular cross.
   *
   * @param length of cross
   * @param path of another
   *
   * @return path
   */
  def createRegularCross(length: Float, path: GeneralPath = new GeneralPath) = {

    path.moveTo(-length, 0)
    path.lineTo(length, 0)
    path.closePath

    path.moveTo(0, -length)
    path.lineTo(0, length)
    path.closePath

    path

  }
}

