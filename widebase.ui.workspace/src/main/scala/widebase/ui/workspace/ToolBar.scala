package widebase.ui.workspace

import javax.swing. { JToolBar, SwingConstants }

import scala.collection.mutable.LinkedHashMap
import scala.swing. { Button, Component, Publisher, Separator }

 /* A tool bar with and id/button map.
 *
 * @author myst3r10n
 */
class ToolBar(n: String, o: Int) extends Component with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(o: Int) = this("", o)
  def this(n: String) = this(n, SwingConstants.HORIZONTAL)

  protected[ToolBar] var map = LinkedHashMap[String, Component]()

  override lazy val peer = new JToolBar(n, o)

  protected trait Share {

    def -=(id: String) = {

      if(!map(id).isInstanceOf[Separator])
        peer.remove(map(id).peer)

      map -= id

      this

    }

    def contains(id: String) = map.contains(id)

  }

  object button extends Share {

    def apply(id: String) = map(id).asInstanceOf[Button]

  }

  object separator extends Share {

    def apply(id: String) = map(id).asInstanceOf[Separator]

  }

  def +=(pair: (String, Component)) = {

    if(pair._2.isInstanceOf[Button]) {

      val button = pair._2.asInstanceOf[Button]
      map += pair._1 -> button
      peer.add(button.peer)

    } else if(pair._2.isInstanceOf[Separator]) {

      map += pair._1 -> pair._2.asInstanceOf[Separator]
      peer.addSeparator

    }

    this

  }

  def prepend(pair: (String, Component)) = {

    if(pair._2.isInstanceOf[Button]) {

      val button = pair._2.asInstanceOf[Button]
      map += pair._1 -> button
      peer.add(button.peer, 0)

    } else if(pair._2.isInstanceOf[Separator]) {

      val separator = pair._2.asInstanceOf[Separator]
      map += pair._1 -> separator
      peer.add(new javax.swing.JToolBar.Separator, 0)

    }

    this

  }

  protected def prepend[K, V](
    map: LinkedHashMap[K, V],
    pair: (K, V)): LinkedHashMap[K, V] = {

    val copy = map.toMap
    map.clear
    map += pair
    map ++= copy

  }
}

