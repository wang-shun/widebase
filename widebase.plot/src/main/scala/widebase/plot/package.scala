package widebase

import java.lang.management.ManagementFactory

import plot.ui. { PlotFrame, PlotPanel }

import org.jfree.data.time.TimeSeriesCollection

import scala.swing.SimpleSwingApplication

/** Plot package.
 *
 * @author myst3r10n
 */
package object plot {

  def show(collection: TimeSeriesCollection) {

    class Swing extends SimpleSwingApplication {

      def top = new PlotFrame(PlotPanel(collection))

    }

    (new Swing).startup(
      scala.collection.JavaConversions.collectionAsScalaIterable(
        ManagementFactory.getRuntimeMXBean.getInputArguments).toArray)

  }
}

