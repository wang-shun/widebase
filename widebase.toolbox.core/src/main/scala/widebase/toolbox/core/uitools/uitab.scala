package widebase.toolbox.core.uitools

import impl. { Tab, TabGroup }

import javax.swing.ImageIcon

/** UI tab.
 *
 * @author myst3r10n
 */
object uitab {

  /** Creates a parentless tab.
   *
   * @param properties of tab
   *
   * @return handle of tab
   */
  def apply(properties: Any*): Tab = uitab(null, properties:_*)

  /** Creates a tab into tab group.
   *
   * @param group of tab
   * @param properties of tab
   *
   * @return handle of tab
   */
  def apply(group: TabGroup, properties: Any*) = {

    val tab = new Tab

    tab.page.title = "untitled"

    tab.page.icon = new ImageIcon(
      getClass.getResource("/icon/document-multiple.png"))

    tab.page.content = new scala.swing.BorderPanel

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "Title" => tab.page.title = properties(i).asInstanceOf[String]
        case property => throw new Exception("Property not found: " + property)

      }

      i += 1

    }

    group += tab

    tab

  }
}

