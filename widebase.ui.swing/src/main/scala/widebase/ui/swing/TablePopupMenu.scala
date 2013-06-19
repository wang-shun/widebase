package widebase.ui.swing

import event. {

  TableAddRecord,
  TableInsertRecord,
  TableRemoveRecord

}

import java.util.UUID

import javax.swing.ImageIcon

import moreswing.swing.i18n.LocaleManager

import scala.swing.Separator

/* Popup menu of table.
 *
 * @author myst3r10n
 */
class TablePopupMenu extends PopupMenu {

  this += "Insert Record" -> new MenuItem(new Action("Insert Record") {

    icon = new ImageIcon(getClass.getResource("/icon/insert-table.png"))
    tooltip = LocaleManager.text("Insert Record")

    def apply = { TablePopupMenu.this.publish(TableInsertRecord) }

  } )

  this += "Append Record" -> new MenuItem(new Action("Append Record") {

    icon = new ImageIcon(getClass.getResource("/icon/list-add.png"))
    tooltip = LocaleManager.text("Append Record")

    def apply = { TablePopupMenu.this.publish(TableAddRecord) }

  } )

  this += UUID.randomUUID.toString -> new Separator

  this += "Remove Record" -> new MenuItem(new Action("Remove Record") {

    icon = new ImageIcon(getClass.getResource("/icon/list-remove.png"))
    tooltip = LocaleManager.text("Remove Record")

    def apply = { TablePopupMenu.this.publish(TableRemoveRecord) }

  } )
}

