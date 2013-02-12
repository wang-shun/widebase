package widebase.ui.table

import widebase.db.table.Table

/** Table panel.
 *
 * @param model0 of table
 *
 * @author myst3r10n
 */
case class TablePanel(model0: TableModelLike) extends scala.swing.Table {

  def this(table: Table) = this(TableModel(table))
  def this(tables: Array[Table]) = this(TableModelParted(tables))

  super.model = model0

  override def model = model0

  peer.setColumnSelectionAllowed(true)

}

