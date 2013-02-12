package widebase.ui.table

import event. { TableAddRecord, TableInsertRecord, TableRemoveRecord }

import javax.swing.JToolBar
import javax.swing. { ImageIcon, SwingConstants }

import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, Publisher }
import scala.swing.event.ButtonClicked

/** Tool bar of frame.
 *
 * @param name of tool bar
 * @param orientation of tool bar
 *
 * @author myst3r10n
 */
class TableToolBar(
  name: String,
  orientation: Int)
  extends JToolBar(name, orientation) with Publisher {

  def this() = this("", SwingConstants.HORIZONTAL)
  def this(orientation: Int) = this("", orientation)
  def this(name: String) = this(name, SwingConstants.HORIZONTAL)

  setFloatable(false)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/insert-table.png"))
    tooltip = LocaleManager.text("Insert_record")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => TableToolBar.this.publish(TableInsertRecord)

    }
  } ).peer)

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/list-add.png"))
    tooltip = LocaleManager.text("Add_record")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => TableToolBar.this.publish(TableAddRecord)

    }
  } ).peer)

  addSeparator

  add((new Button {

    icon = new ImageIcon(getClass.getResource("/icon/list-remove.png"))
    tooltip = LocaleManager.text("Remove_record")

    listenTo(this)
    reactions += {

      case ButtonClicked(_) => TableToolBar.this.publish(TableRemoveRecord)

    }
  } ).peer)
}

