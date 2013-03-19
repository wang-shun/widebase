package widebase.workspace

import moreswing.swing.i18n.LMainFrame

/** A common trait to build application frames.
 * 
 * @author myst3r10n
 */
abstract class FrameLike extends LMainFrame {

  override def menuBar: MenuBar = {

    super.menuBar.asInstanceOf[MenuBar]

  }

  protected var _preferences: PreferenceManager = null

  def preferences = _preferences

  def preferences_=(t: PreferenceManager) {

    _preferences = t

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

