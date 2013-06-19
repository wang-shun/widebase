package widebase.ui.chart.annotations

import java.awt. { Color, Stroke }
import java.awt.geom.Rectangle2D

import org.jfree.chart.annotations.XYShapeAnnotation

/** Rectangle annotation.
 *
 * @author myst3r10n
 */
object Rectangle {

  /** Perform rectangle.
   *
   * @param x position
   * @param y position
   * @param w weight
   * @param h height
   * @param properties of rectangle
   *
   * @return annotation
   */
  def apply(x: Any, y: Any, w: Any, h: Any, properties: Any*) = {

    var edgeColor = Color.BLACK
    var faceColor: Color = null
    var lineWidth = 1.0f
    var stroke: Stroke = null

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "EdgeColor" => edgeColor = Color.decode(properties(i).asInstanceOf[String])
        case "FaceColor" => faceColor = Color.decode(properties(i).asInstanceOf[String])
        case "-" =>
        case "--" => stroke = LineStyle.dash(lineWidth)
        case ":" => stroke = LineStyle.dot(lineWidth)
        case "-." => stroke = LineStyle.dashDot(lineWidth)
        case "LineWidth" => lineWidth = properties(i).asInstanceOf[Float]
        case _ =>

      }

      i += 1

    }

    if(stroke == null)
      stroke = LineStyle.solid(lineWidth)

    new XYShapeAnnotation(
      new Rectangle2D.Double(asDouble(x), asDouble(y), asDouble(w), asDouble(h)),
      stroke,
      edgeColor,
      faceColor)

  }
}

