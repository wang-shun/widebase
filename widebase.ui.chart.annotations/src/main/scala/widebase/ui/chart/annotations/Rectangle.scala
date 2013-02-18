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
   * @param values of coordinates, properties and format
   *
   * @return annotation
   */
  def apply(values: Any*) = {

    val x = asDouble(values(0))
    val y = asDouble(values(1))
    val w = asDouble(values(2))
    val h = asDouble(values(3))

    var i = 4
    var edgeColor = Color.BLACK
    var faceColor: Color = null
    var lineWidth = 1.0f
    var stroke: Stroke = null

    while(i < values.length) {

      val property = values(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "EdgeColor" => edgeColor = Color.decode(values(i).asInstanceOf[String])
        case "FaceColor" => faceColor = Color.decode(values(i).asInstanceOf[String])
        case "-" =>
        case "--" => stroke = LineStyle.dash(lineWidth)
        case ":" => stroke = LineStyle.dot(lineWidth)
        case "-." => stroke = LineStyle.dashDot(lineWidth)
        case "lineWidth" => lineWidth = values(i).asInstanceOf[Float]
        case _ =>

      }

      i += 1

    }

    if(stroke == null)
      stroke = LineStyle.solid(lineWidth)

    new XYShapeAnnotation(
      new Rectangle2D.Double(x, y, w, h),
      stroke,
      edgeColor,
      faceColor)

  }
}

