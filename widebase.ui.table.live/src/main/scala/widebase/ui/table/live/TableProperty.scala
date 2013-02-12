package widebase.ui.table.live

import widebase.ui.table.TablePanel

/** Table properties.
 *
 * @author myst3r10n
 */
object TableProperty {

  /** Perform a property.
   *
   * @param panel of table
   * @param property name
   * @param value of property
   **/
  def apply(panel: TablePanel, property: String, value: Any) {

    property match {

      case _ => throw new Exception("Property not found: " + property)

    }
  }
}

