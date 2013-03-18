package widebase.ui.workspace

import scala.collection.mutable.LinkedHashMap

import scala.swing. {

  AbstractButton,
  Component,
  RadioMenuItem,
  Separator,
  SequentialContainer

}

/* A common trait of id/menu map.
 *
 * @author myst3r10n
 */
trait MenuLike extends SequentialContainer {

  protected[MenuLike] val map = LinkedHashMap[String, Component]()

  protected trait Share {

    def -=(id: String) = {

      contents -= map(id)
      map -= id

      this

    }

    def contains(id: String) = map.contains(id)

  }

  object item extends Share {

    def apply(id: String) = map(id).asInstanceOf[MenuItem]

  }

  object radio extends Share {

    def apply(id: String) = map(id).asInstanceOf[RadioMenuItem]

  }

  object separator extends Share {

    def apply(id: String) = map(id).asInstanceOf[Separator]

  }

  object sub extends Share {

    def apply(id: String) = map(id).asInstanceOf[Menu]

  }

  def +=(id: String) = {

    map += id -> new Separator
    contents += map.values.last

    this

  }

  def +=(pair: (String, Component)) = {

    map += pair._1 -> pair._2
    contents += map.values.last

    this

  }

  def prepend(id: String): MenuLike = {

    prepend(map, id -> new Separator)
    contents.prepend(map.values.head)

    this

  }

  def prepend(pair: (String, AbstractButton)): MenuLike = {

    prepend(map, pair._1 -> pair._2)
    contents.prepend(map.values.head)

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

