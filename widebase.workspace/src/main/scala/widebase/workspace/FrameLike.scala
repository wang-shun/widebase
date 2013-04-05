package widebase.workspace

import moreswing.swing.i18n.LMainFrame

import scala.swing.BorderPanel

/** A common trait to build application frames.
 * 
 * @author myst3r10n
 */
abstract class FrameLike extends LMainFrame {

  override def menuBar: MenuBar = {

    super.menuBar.asInstanceOf[MenuBar]

  }

  protected var _panel: BorderPanel = null

  def panel = _panel

  def panel_=(p: BorderPanel) {

    _panel = p

  }

  protected var _preferences: PreferenceManager = null

  def preferences = _preferences

  def preferences_=(p: PreferenceManager) {

    _preferences = p

  }

  protected var _toolBar: ToolBar = null

  def toolBar = _toolBar

  def toolBar_=(t: ToolBar) {

    _toolBar = t

  }

  protected var _pagedPane: PagedPane = null

  def pagedPane = _pagedPane

  def pagedPane_=(p: PagedPane) {

    _pagedPane = p

  }
}

