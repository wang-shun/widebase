package widebase.toolbox.core.uitools

import impl. { Tab, TabGroup }

import java.awt.BorderLayout

import scala.swing. {

  Frame,
  RootPanel,
  SequentialContainer,
  UIElement

}

/** UI tab group.
 *
 * @author myst3r10n
 */
object uitabgroup {

  import widebase.toolbox.core.graphics.gcf

  /** Creates a tab group into current figure.
   * If no figure exists, creates one.
   *
   * @param properties of tab group
   *
   * @return tab group handle
   */
  def apply(properties: Any*): TabGroup = uitabgroup(gcf, properties:_*)

  /** Creates a tab group into parent element.
   *
   * @param parent element
   * @param properties of tab group
   *
   * @return tab group handle
   */
  def apply(parent: UIElement, properties: Any*) = {

    val groupPanel = new TabGroup

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case property => throw new Exception("Property not found: " + property)

      }

      i += 1

    }

    parent match {

      case null => // headless
      case parent: Frame =>

        parent.peer.getContentPane.add(groupPanel.peer, BorderLayout.CENTER)
        parent.peer.revalidate
        parent.peer.repaint()

      case parent: RootPanel => parent.contents = groupPanel
      case parent: SequentialContainer => parent.contents += groupPanel
      case parent: Tab => parent.content = groupPanel

    }

    groupPanel

  }
}

