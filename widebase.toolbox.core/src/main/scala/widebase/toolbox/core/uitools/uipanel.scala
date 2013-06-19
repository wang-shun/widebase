package widebase.toolbox.core.uitools

import impl.Tab

import scala.swing.GridPanel

import scala.swing. { RootPanel, SequentialContainer, UIElement }

/** Panel container.
 *
 * @author myst3r10n
 */
object uipanel {

  import widebase.toolbox.core.graphics.gcf

  /** Create panel into current figure.
   *
   * @param properties of panel
   *
   * @return panel handle
   */
  def apply(properties: Any*): GridPanel = uipanel(gcf, properties:_*)

  /** Create panel into parent element.
   *
   * @param parent element
   * @param properties of panel
   *
   * @return panel handle
   */
  def apply(parent: UIElement, properties: Any*) = {

    val groupPanel = new GridPanel(1, 1)

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case _ => throw new Exception("Property not found: " + property)

      }

      i += 1

    }

    parent match {

      case null => // headless
      case parent: RootPanel => parent.contents = groupPanel
      case parent: SequentialContainer => parent.contents += groupPanel
      case parent: Tab => parent.content = groupPanel

    }

    groupPanel

  }
}

