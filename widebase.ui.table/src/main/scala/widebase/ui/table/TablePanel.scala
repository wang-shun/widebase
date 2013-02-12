package widebase.ui.table

import javax.swing.ListSelectionModel
import javax.swing.table.DefaultTableModel

import widebase.db.table.Table

/** Table panel.
 *
 * @param model0 of table
 *
 * @author myst3r10n
 */
case class TablePanel(model0: DefaultTableModel) extends scala.swing.Table {

  def this(table: Table) = this(TableModel(table))
  def this(tables: Array[Table]) = this(TableModelParted(tables))

  model = model0

  peer.setColumnSelectionAllowed(true)

}

