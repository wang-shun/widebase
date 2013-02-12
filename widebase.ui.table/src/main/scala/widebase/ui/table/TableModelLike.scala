package widebase.ui.table

import javax.swing.table.DefaultTableModel

import widebase.db.table.Table

/** A common trait for table models.
 *
 * @author myst3r10n
 */
trait TableModelLike extends DefaultTableModel {

  val table: Table
  val tables: Array[Table]

}

