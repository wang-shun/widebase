package widebase.toolbox.core.uitools.impl

import widebase.ui.swing.TabbedPane

import scala.swing. { Component, Publisher }

/** A tab group
 *
 * @author myst3r10n
 */
class TabGroup(values: Any*) extends Component with Publisher {

  /** The [[moreswing.swing.TabbedPane]] object. */
  lazy val pane = new TabbedPane

  override lazy val peer = pane.peer

  def +=(tab: Tab) = {

    pane.pages += tab.page

    this

  }
}

