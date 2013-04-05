package widebase.workspace.runtime

import moreswing.swing.TabbedDesktopPane

abstract class PluginLike {

  val category: String
  val homepage: String
  val id: String
  val name: String

  def option: Option[TabbedDesktopPane.Page] = None

  def register {

    plugin += id -> this

  }

  def unregister {

    plugin -= id

  }
}

