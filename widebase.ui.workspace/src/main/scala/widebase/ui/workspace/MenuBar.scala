package widebase.ui.workspace

import scala.collection.mutable.LinkedHashMap

/* A menu bar with and id/menu map.
 *
 * @author myst3r10n
 */
class MenuBar extends scala.swing.MenuBar {

  protected val map = LinkedHashMap[String, Menu]()

  def +=(pair: (String, Menu)) = {

    map += pair
    contents += map.values.last

    this

  }

  def -=(id: String) = {

    contents -= map(id)
    map -= id

    this

  }

  def apply(id: String) = map(id)
  def contains(id: String) = map.contains(id)

}

