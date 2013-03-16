package widebase.ui.toolkit.runtime

import moreswing.swing.TabbedDesktopPane

abstract class PluginLike {

  val label: String
  val scope: String

  def option: Option[TabbedDesktopPane.Page]
  def register: Unit
  def unregister: Unit

}

