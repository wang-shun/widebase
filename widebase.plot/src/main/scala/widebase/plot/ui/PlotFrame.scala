package widebase.plot.ui

import event. {

  PlotZoomIn,
  PlotZoomInX,
  PlotZoomInY,
  PlotZoomOut,
  PlotZoomOutX,
  PlotZoomOutY

}

import java.awt.BorderLayout

import javax.swing.JOptionPane

import moreswing.swing.i18n.LMainFrame

import net.liftweb.common.Logger

import org.joda.time.format.DateTimeFormat

import scala.swing. { BorderPanel, Component, Dimension, ScrollPane }

/** Main frame.
 * 
 * @author myst3r10n
 */
class PlotFrame(plotPanel: PlotPanel) extends LMainFrame with Logger {

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")

  var target = ""

  val toolBar = new PlotToolBar

  val scrollPane = new ScrollPane {

    contents = new Component {

      PlotFrame.this.deafTo(PlotFrame.this.plotPanel)
      PlotFrame.this.listenTo(plotPanel)

      override lazy val peer = plotPanel

    }
  }

  contents = new scala.swing.BorderPanel {

    peer.add(toolBar, BorderLayout.NORTH)
    add(scrollPane, BorderPanel.Position.Center)

  }

  listenTo(this, toolBar)

  reactions += {

    case PlotZoomIn => plotPanel.zoomInBoth(0.0, 0.0)
    case PlotZoomInX => plotPanel.zoomInDomain(0.0, 0.0)
    case PlotZoomInY => plotPanel.zoomInRange(0.0, 0.0)
    case PlotZoomOut => plotPanel.zoomOutBoth(0.0, 0.0)
    case PlotZoomOutX => plotPanel.zoomOutDomain(0.0, 0.0)
    case PlotZoomOutY => plotPanel.zoomOutRange(0.0, 0.0)

  }
}

