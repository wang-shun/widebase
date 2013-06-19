package widebase.toolbox.core.graphics.impl

import moreswing.swing.i18n.LFrame

import scala.swing.event.WindowClosing

/** Frame of figure.
 *
 * @param id of figure
 *
 * @author myst3r10n
 */
class FigureFrame(val id: String) extends LFrame {

  object default extends DefaultProperties

  val toolBar = new FigureToolBar

  peer.getContentPane.add(toolBar.peer, java.awt.BorderLayout.NORTH)

  protected var _currentAxes: AxesPanel = _

  def currentAxes = _currentAxes

  def currentAxes_=(axes: AxesPanel) {

    _currentAxes = axes

  }

  reactions += {

    case WindowClosing(source) => source.dispose

  }
}

