package widebase.workspace.runtime

import moreswing.swing.TabbedDesktopPane

trait PluginLike{

  val category: String
  val homepage: String
  val id: String
  val name: String

  def option: Option[TabbedDesktopPane.Page] = None

  def register {

    plugin += id -> this

  }

  def startup {}

  def unregister {

    plugin -= id

  }
}

