package widebase.workspace.ide.editor

import event.PageRename

import java.awt.event. { InputEvent, KeyEvent }
import java.util.UUID

import javax.swing. { ImageIcon, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing. { Button, ScrollPane, Separator }

import widebase.workspace. {

  Action,
  FrameLike,
  Menu,
  MenuItem,
  PagedPane,
  PageMenu,
  PreferenceManager

}

import widebase.workspace.runtime.PluginLike

class Plugin(frame: FrameLike) extends PluginLike {

  import widebase.workspace. { runtime, util }

  class NewEdit(title0: String = "") extends Action(title0) {

    import scala.util.control.Breaks. { break, breakable }

    mnemonic = KeyEvent.VK_N
    accelerator = Some(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK))
    toolTip = "New_Edit"

    def apply {

      if(frame.pagedPane.selection.index == -1 ||
         !frame.pagedPane.selection.page.content.isInstanceOf[PagedPane] ||
         frame.pagedPane.selection.page.content.isInstanceOf[PreferenceManager])
       frame.pagedPane.add(content = configure(new PagedPane))
      else if(frame.pagedPane.selection.page.content.asInstanceOf[PagedPane].selection.index == -1)
        configure(frame.pagedPane.selection.page.content.asInstanceOf[PagedPane])

      val pane = frame.pagedPane.selection.page.content.asInstanceOf[PagedPane]

      var count = 0
      var found = true

      do {

        count += 1
        found = true

        breakable {

          pane.pages.foreach { page =>

            if(page.title == LocaleManager.text("Edit_?", count)) {

              found = false
              break

            }
          }
        }
      } while(!found)

      val panel = new EditPanel

      util.bind(
        panel,
        KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK),
        "Open",
        { panel.toolBar.button("Open").action.apply } )

      util.bind(
        panel,
        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
        "Save",
        { panel.toolBar.button("Save").action.apply } )

      util.bind(
        panel,
        KeyStroke.getKeyStroke("F9"),
        "Content",
        { panel.toolBar.button("Content").action.apply } )

      util.bind(
        panel,
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_MASK),
        "Selection",
        { panel.toolBar.button("Selection").action.apply } )

      val page = new TabbedDesktopPane.Page(
        LocaleManager.text("Edit_?", count),
        new ImageIcon(getClass.getResource("/icon/text-plain.png")),
        panel)

      panel.listenTo(panel)

      panel.reactions += {

        case PageRename => pane.selection.page.title = panel.currentFile.getName

      }

      pane.pages += page
      panel.codePane.editor.requestFocus

    }

    protected def configure(pane: PagedPane) = {

      // Key bindings

      for(i <- 0 to 8)
        util.bind(
          pane,
          KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.ALT_MASK),
          "PageSelection" + i,
          () => {

            if(i < pane.pages.size) {

              pane.selection.index = i

              if(pane.selection.page.content.isInstanceOf[EditPanel])
                pane.selection.page.content.asInstanceOf[EditPanel]
                  .codePane.editor.requestFocus

            }
          }
        )

      util.bind(
        pane,
        KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_MASK),
        "PageSelection9",
        () => {

          if(9 < pane.pages.size) {

            pane.selection.index = 9
            pane.selection.page.content.asInstanceOf[EditPanel]
              .codePane.editor.requestFocus

          }
        }
      )

      // Popup menu

      if(!pane.popupMenu.sub.contains("New")) {

        pane.popupMenu.prepend(UUID.randomUUID.toString)
        pane.popupMenu.prepend("New" -> new Menu("New"))

      }

      pane.popupMenu.sub("New").prepend(
        "Edit" -> new MenuItem(new NewEdit("Edit")) {

          icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

        } )

      pane.popupMenu += UUID.randomUUID.toString -> new Separator

      if(!pane.popupMenu.item.contains("Close"))
        pane.popupMenu += "Close" -> new MenuItem(new Action("Close") {

            def apply = { pane.pages.remove(pane.mouseOverTab) }

        } )

      if(!pane.popupMenu.item.contains("InactiveOnly"))
        pane.popupMenu += "InactiveOnly" -> new MenuItem(new Action("Inactive_Only") {

            def apply = { pane.pages.removeInactive }

        } )

      pane

    }
  }

  val category = Plugin.category
  val homepage = Plugin.homepage
  val id = Plugin.id
  val name = Plugin.name

  override def option = Some(
    new TabbedDesktopPane.Page(
      LocaleManager.text("Editor"),
      new ImageIcon(getClass.getResource("/icon/configure.png")),
      new ScrollPane {

        contents = new scala.swing.Label("N/A")

    } )
  )

  override def register {

    frame.menuBar("File").sub("New").prepend(
      "Edit" -> new MenuItem(new NewEdit("Edit")) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    frame.toolBar.prepend(
      "Edit" -> new Button(new NewEdit) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    frame.pagedPane.popupMenu.sub("New").prepend(
      "Edit" -> new MenuItem(new NewEdit("Edit")) {

        icon = new ImageIcon(getClass.getResource("/icon/tab-new.png"))

      }
    )

    super.register

  }
}

object Plugin {

  val category = "Core"
  val homepage = "http://widebase.github.com/"
  val id = classOf[Plugin].getPackage.getName
  val name = "Widebase IDE Editor"

}

