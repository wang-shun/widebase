package widebase.ui.chart.annotations

import java.awt.BasicStroke

/** Line style.
 *
 * @author myst3r10n
 */
object LineStyle {

    /** Dash line.
     *
     * @param width of line
     *
     * @return line style
     */
    def dash(width: Float) = new BasicStroke(
      width,
      BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER,
      10.0f,
      Array(10.0f),
      0.0f)

    /** Dot line.
     *
     * @param width of line
     *
     * @return line style
     */
    def dot(width: Float) = new BasicStroke(
      width,
      BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER,
      1.0f,
      Array(2.0f),
      0.0f)

    /** Dash dot line.
     *
     * @param width of line
     *
     * @return line style
     */
    def dashDot(width: Float) = new BasicStroke(
      width,
      BasicStroke.CAP_BUTT,
      BasicStroke.JOIN_MITER,
      1.0f,
      Array(8.0f, 3.0f, 2.0f, 3.0f),
      0.0f)

    /** Solid line.
     *
     * @param width of line
     *
     * @return line style
     */
    def solid(width: Float) = new BasicStroke(width)

}

