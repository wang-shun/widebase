package widebase.ui.toolkit

import event.EventForwarder

import javax.swing. { JToolBar, SwingConstants }

import scala.collection.mutable.LinkedHashMap
import scala.swing. { Button, Component, Publisher, Separator }

/** A tool bar.
 * 
 * @author myst3r10n
 */
abstract class ToolBarLike(n: String, o: Int) extends Component with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(o: Int) = this("", o)
  def this(n: String) = this(n, SwingConstants.HORIZONTAL)

  override lazy val peer = new JToolBar(n, o)

  protected val items: LinkedHashMap[String, Any]

  def setup {

    EventForwarder(this, items)

    items.values.foreach(item =>
      if(item.isInstanceOf[Separator])
        peer.addSeparator
      else if(item.isInstanceOf[Button])
        peer.add(item.asInstanceOf[Button].peer))

  }
}

