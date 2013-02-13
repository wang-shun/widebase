package widebase.ui

import moreswing.swing.i18n.LFrame

import scala.swing.event.WindowClosing

/** Frame of figure.
 * 
 * @author myst3r10n
 */
trait FigureFrame extends LFrame {

  val figure: Int

  reactions += {

    case WindowClosing(source) => source.dispose

  }
}

