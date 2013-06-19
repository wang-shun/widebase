package widebase.ui.chart.annotations

import java.awt. { Color, Stroke }

import org.jfree.chart.annotations.XYLineAnnotation

/** Line annotation.
 *
 * @author myst3r10n
 */
object Line {

  /** Perform line.
   *
   * @param x position pair of X1 and X2
   * @param y position pair of Y1 and Y2
   * @param properties of line
   *
   * @return annotation
   */
  def apply(x: Pair[Any, Any], y: Pair[Any, Any], properties: Any*) = {

    var color = Color.BLACK
    var lineWidth = 1.0f
    var stroke: Stroke = null

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "Color" =>
          color = Color.decode(properties(i).asInstanceOf[String])
          i += 1

        case "-" =>
        case "--" => stroke = LineStyle.dash(lineWidth)
        case ":" => stroke = LineStyle.dot(lineWidth)
        case "-." => stroke = LineStyle.dashDot(lineWidth)
        case "LineWidth" =>

          lineWidth = properties(i).asInstanceOf[Float]
          i += 1

        case _ =>

      }
    }

    if(stroke == null)
      stroke = LineStyle.solid(lineWidth)

    new XYLineAnnotation(
      asDouble(x._1),
      asDouble(y._1),
      asDouble(x._2),
      asDouble(y._2),
      stroke,
      color)

  }
}

