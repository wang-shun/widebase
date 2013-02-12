package widebase.ui.table

import moreswing.swing.i18n.LocaleManager

import scala.collection.mutable.HashMap
import scala.swing.event.WindowClosing
import scala.util.control.Breaks. { break, breakable }

import widebase.db.table.Table

/** Interactive tables.
 *
 * @author myst3r10n
 */
package object live {

  /** Hold all sheets global. */
  protected val sheets = HashMap[Int, SheetFrame]()

  /** Current sheet. */
  var sheet = 1

  /** Close all sheets. */
  def clear {

    sheets.values.foreach(_.close)
    sheets.clear

  }

  /** Show table sheet.
   *
   * @param values of data, properties and format
   *
   * @return sheet frame
   */
  def uitable(values: Any*) = show(uitablePanel(values:_*))

  /** Table panel.
   *
   * @param values of data, properties and format
   *
   * @return table panel
   */
  def uitablePanel(values: Any*) = {

    val table =
      if(values.head.isInstanceOf[Table])
        TablePanel(TableModel(values.head.asInstanceOf[Table]))
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

              case _ =>

            }

            // Resolve generic properties
            TableProperty(table, property, values(i))

          i += 1

        } else
          break

      }
    }

    table

  }

  /** Show sheet.
   *
   * @param panel of table
   *
   * @return table frame
   */
  protected def show(panel: TablePanel) = {

    if(!sheets.contains(sheet)) {

      val frame = new SheetFrame(panel, 800, 600, sheet) {

        title = LocaleManager.text("sheet.title_?", sheet)

        reactions += {

          case WindowClosing(source) =>
            val frame = source.asInstanceOf[SheetFrame]
            sheets -= frame.sheet
            source.dispose

        }
      }

      frame.pack
      frame.visible = true

      sheets += sheet -> frame

    } else
      sheets(sheet).set(panel)

    sheets(sheet)

  }
}

