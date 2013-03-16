package widebase.ui.ide.editor

import event. { EditRename, EditSelection }

import java.awt.event. { InputEvent, KeyEvent }
import java.util.UUID

import javax.swing. { ImageIcon, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, ScrollPane, Separator }

import widebase.ui.toolkit. {

  Action,
  FrameLike,
  Menu,
  MenuItem,
  PreferenceManager,
  ViewPane

}

import widebase.ui.toolkit.runtime.PluginLike

class Plugin(frame: FrameLike) extends PluginLike {

  import widebase.ui.toolkit.runtime

  class EditNew(title0: String = "") extends Action(title0) {

    import scala.util.control.Breaks. { break, breakable }
    import widebase.ui.toolkit

    mnemonic = KeyEvent.VK_N
    accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK))
    toolTip = "New_Edit"

    def apply {

      if(frame.viewPane.selection.index == -1 ||
         !frame.viewPane.selection.page.content.isInstanceOf[ViewPane] ||
         frame.viewPane.selection.page.content.isInstanceOf[PreferenceManager]) {

        val viewSubPane = new ViewPane {

          popupMenu.prepend(UUID.randomUUID.toString)
          popupMenu.prepend("New" -> new Menu("New"))

          popupMenu += UUID.randomUUID.toString -> new Separator

          popupMenu += "Close" -> new MenuItem(new Action("Close") {

              def apply = { pages.remove(mouseOverTab) }

          } )

          popupMenu += "Inactive_Only" -> new MenuItem(new Action("Inactive_Only") {

              def apply = { pages.removeInactive }

          } )

        }

        frame.viewPane.add(view = viewSubPane)

      }

      val viewSubPane = frame.viewPane.selection.page.content.asInstanceOf[ViewPane]

      if(!viewSubPane.popupMenu.sub("New").item.contains("Edit"))
        viewSubPane.popupMenu.sub("New").prepend(
          "Edit" -> new MenuItem(new EditNew("Edit")) {

            icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

          } )

      var found = true
      var editCount = 0

      do {

        found = true
        editCount += 1

        breakable {

          viewSubPane.pages.foreach { page =>

            if(page.title == LocaleManager.text("Edit_?", editCount)) {

              found = false
              break

            }
          }
        }
      } while(!found)

      val edit = new EditPanel

      val page = new TabbedDesktopPane.Page(
        LocaleManager.text("Edit_?", editCount),
        new ImageIcon(getClass.getResource("/icon/text-plain.png")),
        edit)

      edit.listenTo(edit)

      edit.reactions += {

        case EditRename =>
          viewSubPane.selection.page.title = edit.currentFile.getName

        case event: EditSelection =>

          if(event.index < viewSubPane.pages.size) {

            viewSubPane.selection.index = event.index
            viewSubPane.selection.page.content.asInstanceOf[EditPanel].codePane.editor.requestFocus

          }

      }

      viewSubPane.pages += page
      edit.codePane.editor.requestFocus

    }
  }

  val label = "Widebase IDE Editor"
  val scope = "widebase.ui.ide.editor"

  def option = Some(
    new TabbedDesktopPane.Page(
      LocaleManager.text("Editor"),
      new ImageIcon(getClass.getResource("/icon/configure.png")),
      new ScrollPane {

        contents = new scala.swing.Label("Under construction...")

    } )
  )

  def register {

    frame.menuBar("File").sub("New").prepend(
      "Edit" -> new MenuItem(new EditNew("Edit")) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    frame.toolBar.prepend(
      "Edit" -> new Button(new EditNew) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    frame.viewPane.popupMenu.sub("New").prepend(
      "Edit" -> new MenuItem(new EditNew("Edit")) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    runtime.plugin += "widebase.ui.ide.editor" -> this

  }

  def unregister {

    runtime.plugin -= "widebase.ui.ide.editor"

  }
}

