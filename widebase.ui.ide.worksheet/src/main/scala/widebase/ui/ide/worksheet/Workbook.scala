package widebase.ui.ide.worksheet

import javax.swing.ImageIcon

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n. { LAction, LMenu, LocaleManager }

import scala.swing. { Action, Menu, MenuItem }

import widebase.ui.ide.event. { NewEdit, NewWorksheet }
import widebase.ui.toolkit.worksheet.WorkbookLike

/** Managed worksheets.
 * 
 * @author myst3r10n
 */
class Workbook extends WorkbookLike {

  import scala.swing.TabbedPane.Layout
  import moreswing.swing.TabbedPane.Page

  restore

  object New extends Menu("New") with LMenu {


    object Edit extends MenuItem("") {

      action = new Action("Edit") with LAction {

        tooltip = LocaleManager.text("New_Edit")
        def apply = { Workbook.this.publish(NewEdit) }

      }

      icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

    }

    contents += Edit

  }

  popupMenu.contents.prepend(New)

  private var worksheetCount = BigInt(0)

  def newWorksheet {

    if(pages.isEmpty)
      worksheetCount = 1
    else
      worksheetCount += 1

    pages += new TabbedDesktopPane.Page(
      LocaleManager.text("Worksheet_?", worksheetCount),
      new ImageIcon(getClass.getResource("/icon/document-multiple.png")),
      new Worksheet)

  }
}

