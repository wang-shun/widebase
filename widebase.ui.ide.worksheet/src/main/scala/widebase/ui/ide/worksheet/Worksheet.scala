package widebase.ui.ide.worksheet

import javax.swing.ImageIcon

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n. { LAction, LMenu, LocaleManager }

import scala.swing. { Action, Menu, MenuItem }

import widebase.ui.ide.event. { EditRename, EditSelection }
import widebase.ui.toolkit.worksheet.WorksheetLike

/** Managed worksheet pages.
 * 
 * @author myst3r10n
 */
class Worksheet extends WorksheetLike {

  restore

  object New extends Menu("New") with LMenu {

    object Edit extends MenuItem("") {

      action = new Action("Edit") with LAction {

        def apply = { newEdit }

      }
    }

    contents += Edit

  }

  popupMenu.contents.prepend(New)

  private var editCount = BigInt(0)

  def newEdit {

    if(pages.isEmpty)
      editCount = 1
    else
      editCount += 1

    val edit = new EditPanel

    val page = new TabbedDesktopPane.Page(
      LocaleManager.text("Edit_?", editCount),
      new ImageIcon(getClass.getResource("/icon/text-plain.png")),
      edit)

    listenTo(this, edit)

    reactions += {

      case event: EditRename => selection.page.title = event.replace

      case event: EditSelection =>

        if(event.index < pages.size) {

          selection.index = event.index
          selection.page.content.asInstanceOf[EditPanel].codePane.editor.requestFocus

        }

    }

    pages += page
    edit.codePane.editor.requestFocus

  }
}

