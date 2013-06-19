package widebase.toolbox.core.uitools.impl

import widebase.ui.swing.TabbedPane

import scala.swing. { Component, Publisher }

/** A tab
 *
 * @author myst3r10n
 */
class Tab extends Component with Publisher {

  /** The [[moreswing.swing.TabbedPane.Page]] object. */
  lazy val page = new TabbedPane.Page(null, null, null)

  def content = page.content

  def content_=(c: Component) {

    page.content = c

  }
}

