package widebase.toolbox.core.uitools

import impl.Tab

import scala.swing. {

  Component,
  Publisher,
  RootPanel,
  SequentialContainer,
  UIElement

}

import widebase.collection.mutable.Reference
import widebase.db.table. { PartitionMap, Table }

import widebase.ui.swing. {

  PopupMenu,
  TableModel,
  TableModelParted,
  TablePane,
  TablePopupMenu

}

/** UI Table.
 *
 * @author myst3r10n
 */
object uitable {

  import widebase.toolbox.core.graphics.gcf

  /** Required by scaladoc. */
  protected object ignore

  /** Creates table into current figure.
   * If no figure exists, creates one.
   *
   * @param properties of table
   *
   * @return table handle
   */
  def apply(properties: Any*): TablePane = uitable(gcf, properties:_*)

  /** Creates table into parent element.
   *
   * @param parent element
   * @param properties of table
   *
   * @return table handle
   */
  def apply(parent: UIElement, properties: Any*) = {

    val tablePane = new TablePane

    var i = 0

    while(i < properties.length) {

      val property = properties(i).asInstanceOf[String]

      i += 1

      // Resolve native properties
      property match {

        case "Data" =>

          if(properties(i).isInstanceOf[Table])
            tablePane.table.model = TableModel(properties(i).asInstanceOf[Table])
          else if(properties(i).isInstanceOf[PartitionMap])
            tablePane.table.model = TableModelParted(
              properties(i).asInstanceOf[PartitionMap].tables.toArray)
          else
            tablePane.table.model = TableModelParted(properties(i).asInstanceOf[Array[Table]])

        case "UIContextMenu" =>

          tablePane.listenTo(properties(i).asInstanceOf[Publisher])

          tablePane.table.peer.setComponentPopupMenu(
            properties(i).asInstanceOf[PopupMenu].peer)

        case _ => throw new Exception("Property not found: " + property)

      }

      i += 1

    }

    parent match {

      case null => // headless
      case parent: RootPanel => parent.contents = tablePane
      case parent: SequentialContainer => parent.contents += tablePane
      case parent: Tab => parent.content = tablePane

    }

    tablePane

  }

  /** Creates popup menu for table.
   *
   * @return popup menu of table
   */
  def uitablemenu = new TablePopupMenu

}

