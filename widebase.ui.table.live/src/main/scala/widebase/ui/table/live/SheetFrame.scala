package widebase.ui.table.live

import widebase.ui.table. { TableFrame, TablePanel }

/** Frame of sheet.
 * 
 * @param panel1 of table
 * @param width of frame
 * @param height of frame
 * @param sheet number
 *
 * @author myst3r10n
 */
case class SheetFrame(
  panel1: TablePanel,
  width: Int = 800,
  height: Int = 600,
  val sheet: Int)
  extends TableFrame(panel1, width, height)

