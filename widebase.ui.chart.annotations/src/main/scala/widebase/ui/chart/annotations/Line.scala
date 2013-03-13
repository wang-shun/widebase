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
   * @param values of coordinates, properties and format
   *
   * @return annotation
   */
  def apply(values: Any*) = {

    val x = values(0).asInstanceOf[Pair[Any, Any]]
    val y = values(1).asInstanceOf[Pair[Any, Any]]

    var i = 2
    var color = Color.BLACK
    var lineWidth = 1.0f
    var stroke: Stroke = null

    while(i < values.length) {

      val property = values(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "Color" =>
          color = Color.decode(values(i).asInstanceOf[String])
          i += 1

        case "-" =>
        case "--" => stroke = LineStyle.dash(lineWidth)
        case ":" => stroke = LineStyle.dot(lineWidth)
        case "-." => stroke = LineStyle.dashDot(lineWidth)
        case "LineWidth" =>

          lineWidth = values(i).asInstanceOf[Float]
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

