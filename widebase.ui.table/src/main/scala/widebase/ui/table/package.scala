package widebase.ui

import scala.util.control.Breaks. { break, breakable }

import widebase.db.table. { PartitionMap, Table }

/** Tables.
 *
 * @author myst3r10n
 */
package object table {

  /** Table panel.
   *
   * @param values of data, properties and format
   *
   * @return panel
   */
  def uitablePanel(values: Any*) = {

    val table =
      if(values.head.isInstanceOf[Table])
        TablePanel(TableModel(values.head.asInstanceOf[Table]))
      else if(values.head.isInstanceOf[PartitionMap])
        TablePanel(TableModelParted(values.head.asInstanceOf[PartitionMap].tables.toArray))
      else
        TablePanel(TableModelParted(values.head.asInstanceOf[Array[Table]]))

    var i = 1

    while(i < values.length) {

      breakable {

        if(i + 1< values.length &&
          values(i).isInstanceOf[String] &&
          values(i).isInstanceOf[String]) {

          val property = values(i).asInstanceOf[String]

          i += 1

          // Resolve native properties
          property match {

            case _ => throw new Exception("Property not found: " + property)

          }

          i += 1

        } else
          break

      }
    }

    table

  }
}

