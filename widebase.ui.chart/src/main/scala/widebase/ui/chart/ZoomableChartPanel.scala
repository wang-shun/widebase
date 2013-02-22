package widebase.ui.chart

import java.awt.event. { MouseWheelEvent, MouseWheelListener }

/** Extends [[org.jfree.chart.ChartPanel]] with x and y zoom.
 *
 * @author myst3r10n
 */
trait ZoomableChartPanel extends org.jfree.chart.ChartPanel {

  /** Zoomable by mouse wheel. */
  var mouseWheelZoomable = true

  addMouseWheelListener(new MouseWheelListener {

    def mouseWheelMoved(event: MouseWheelEvent) {

      if(mouseWheelZoomable)
        if(event.getUnitsToScroll < 0)
          zoomInBoth(event.getUnitsToScroll, event.getUnitsToScroll)
        else if(event.getUnitsToScroll > 0)
          zoomOutBoth(event.getUnitsToScroll, event.getUnitsToScroll)

    }
  } )
}

