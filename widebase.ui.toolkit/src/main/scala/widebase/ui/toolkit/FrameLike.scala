package widebase.ui.toolkit

import moreswing.swing.i18n.LMainFrame

/** A common trait to build application frames.
 * 
 * @author myst3r10n
 */
abstract class FrameLike extends LMainFrame {

  title = "app.title"

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

  protected var _viewPane: ViewPane = null

  def viewPane = _viewPane

  def viewPane_=(t: ViewPane) {

    _viewPane = t

  }
}

