package widebase.workspace.runtime

import moreswing.swing.TabbedDesktopPane

abstract class PluginLike {

  val label: String
  val scope: String

  def option: Option[TabbedDesktopPane.Page] = None

  def register {

    plugin += scope -> this

  }

  def unregister {

    plugin -= scope

  }
}

