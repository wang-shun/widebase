package widebase.ui.chart.plot

import java.awt.Color

import org.jfree.chart.renderer.xy. {

  AbstractXYItemRenderer,
  XYLineAndShapeRenderer

}

import org.jfree.data.general.Series
import org.jfree.data.xy.AbstractXYDataset
import org.jfree.util.ShapeUtilities

/** Plot format.
 *
 * @author myst3r10n
 */
object PlotFormat {

  /** Perform a format.
   *
   * @param collection of chart
   * @param series of collection
   * @param renderer of plotter
   * @param format itself
   **/
  def apply(
    collection: AbstractXYDataset,
    series: Series,
    renderer: AbstractXYItemRenderer,
    format: String) {

    val title = """;.*;""".r.findAllIn(format).toSeq.lastOption

    if(!title.isEmpty)
      series.setKey(title.get.drop(1).dropRight(1))

    val format2 = """;.*;""".r.replaceAllIn(format, "")

    val color = """[0-6|k|r|g|b|m|c|w]""".r
      .findAllIn(format2).toSeq.lastOption

    if(!color.isEmpty)
      color.get match {

        case "0" | "k" => renderer.setSeriesPaint(collection.getSeriesCount, Color.BLACK)
        case "1" | "r" => renderer.setSeriesPaint(collection.getSeriesCount, Color.RED)
        case "2" | "g" => renderer.setSeriesPaint(collection.getSeriesCount, Color.GREEN)
        case "3" | "b" => renderer.setSeriesPaint(collection.getSeriesCount, Color.BLUE)
        case "4" | "m" => renderer.setSeriesPaint(collection.getSeriesCount, Color.MAGENTA)
        case "5" | "c" => renderer.setSeriesPaint(collection.getSeriesCount, Color.CYAN)
        case "6" | "w" => renderer.setSeriesPaint(collection.getSeriesCount, Color.WHITE)

      }

    val line = """-""".r.findAllIn(format2).toSeq.lastOption

    val style = """[.|+|*|o|x|^]""".r
      .findAllIn(format2).toSeq.lastOption


    if(!style.isEmpty && style.get != "-") {

      if(line.isEmpty && renderer.isInstanceOf[XYLineAndShapeRenderer])
        renderer.asInstanceOf[XYLineAndShapeRenderer]
          .setSeriesLinesVisible(collection.getSeriesCount, false)

      style.get match {

        case "." => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.ui.chart.util.ShapeUtilities.createDot(1.0f))

        case "+" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.ui.chart.util.ShapeUtilities.createRegularCross(6.0f))

        case "*" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.ui.chart.util.ShapeUtilities.createDiagonalCross(
            6.0f,
            widebase.ui.chart.util.ShapeUtilities.createRegularCross(6.0f)))

        case "o" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.ui.chart.util.ShapeUtilities.createCircle(6.0f))

        case "x" => renderer.setSeriesShape(
          collection.getSeriesCount,
          widebase.ui.chart.util.ShapeUtilities.createDiagonalCross(6.0f))

        case "^" => renderer.setSeriesShape(
          collection.getSeriesCount,
          ShapeUtilities.createUpTriangle(6.0f))

      }

      if(renderer.isInstanceOf[XYLineAndShapeRenderer])
        renderer.asInstanceOf[XYLineAndShapeRenderer]
          .setSeriesShapesVisible(collection.getSeriesCount, true)

    }
  }
}

