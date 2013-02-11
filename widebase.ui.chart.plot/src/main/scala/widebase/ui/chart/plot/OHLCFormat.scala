package widebase.ui.chart.plot

import java.awt.Color

import org.jfree.chart.renderer.xy.HighLowRenderer
import org.jfree.data.time.ohlc. { OHLCSeries, OHLCSeriesCollection }

object OHLCFormat {

  /** Format plot.
   *
   * @param collection of chart
   * @param series of collection
   * @param renderer of plotter
   * @param format itself
   **/
  def apply(
    collection: OHLCSeriesCollection,
    series: OHLCSeries,
    renderer: HighLowRenderer,
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
  }
}

