package widebase.toolbox.core.scribe

import javax.swing.SwingUtilities

import org.jfree.chart.annotations.AbstractXYAnnotation
import org.jfree.chart.plot.XYPlot

import widebase.ui.chart.annotations. { Ellipse, Line, Rectangle }
import widebase.toolbox.core.graphics.impl.AxesPanel

/** Create annotation objects.
 *
 * @author myst3r10n
 */
object annotation {

  import widebase.toolbox.core.graphics.gca

  /** Draw annotation into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param typeOf `line`, `ellipse` or `recangle`
   * @param values of position or properties
   *
   * @return annoation handle
   */
  def apply(
    typeOf: String,
    values: Any*): AbstractXYAnnotation =
    this(gca, typeOf, values:_*)

  /** Draw annotation into current axes.
   * If no axes or figure exists, creates one.
   *
   * @param typeOf `ellipse` or `rectangle`
   * @param values of position or properties
   *
   * @return annoation handle
   */
  def apply(
    axes: AxesPanel,
    typeOf: String,
    values: Any*) = {

    val plot = axes.peer.getChart.getPlot
    var annotation: AbstractXYAnnotation = null

    plot match {

      case plot: XYPlot =>

        typeOf match {

          case "line" =>

            annotation = Line(
              values(0).asInstanceOf[Pair[Any, Any]],
              values(1).asInstanceOf[Pair[Any, Any]],
              values.drop(2):_*)

          case "ellipse" =>

            annotation = Ellipse(
              values(0),
              values(1),
              values(2),
              values(3),
              values.drop(4):_*)

          case "rectangle" =>

            annotation = Rectangle(
              values(0),
              values(1),
              values(2),
              values(3),
              values.drop(4):_*)

          case _ => throw new Exception("Type of annotation not found: " + typeOf)

        }

        // Prevents throw of ConcurrentModificationException
        SwingUtilities.invokeLater(new Runnable {

          def run {

            plot.getRenderer(0).addAnnotation(annotation)

          }
        } )
    }

    annotation

  }
}

