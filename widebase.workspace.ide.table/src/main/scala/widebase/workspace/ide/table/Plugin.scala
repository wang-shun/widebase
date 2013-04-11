package widebase.workspace.ide.table

import java.awt.event. { InputEvent, KeyEvent }
import java.util.UUID

import javax.swing. { ImageIcon, JOptionPane, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.actors. { Actor, TIMEOUT }
import scala.swing. { Button, ScrollPane, Separator }

import widebase.db.table.Table
import widebase.ui.table.TableFrame

import widebase.workspace. {

  Action,
  Menu,
  MenuItem,
  PagedPane,
  PageMenu,
  PreferenceManager

}

import widebase.workspace.runtime.PluginLike

class Plugin extends Actor with PluginLike {

  import scala.util.control.Breaks. { break, breakable }
  import widebase.workspace. { runtime, util }

  class NewTable(title0: String = "") extends Action(title0) {

    import widebase.workspace

    toolTip = "New_Table"

    def apply {

      val tableName = JOptionPane.showInputDialog(frame.peer, "Table: ")

      if(tableName != null)
        runtime.queue.add(Some("""

          widebase.workspace.ide.table.plugin ! (
            "table",
            """ + tableName + """, """" + tableName + """")

        """))
    }
  }

  val category = Plugin.category
  val homepage = Plugin.homepage
  val id = Plugin.id
  val name = Plugin.name

  def act {
    loop {

      reactWithin(0) {

        case Abort => action(Abort)
        case TIMEOUT => react { case msg => action(msg) }

      }
    }
  }

  override def option = None

  override def register {

    frame.menuBar("File").sub("New").prepend(
      "Table" -> new MenuItem(new NewTable("Table")) {

        icon = new ImageIcon(getClass.getResource("/icon/accessories-calculator.png"))

      }
    )

    frame.toolBar.prepend(
      "Table" -> new Button(new NewTable) {

        icon = new ImageIcon(getClass.getResource("/icon/accessories-calculator.png"))

      }
    )

    frame.pagedPane.popupMenu.sub("New").prepend(
      "Table" -> new MenuItem(new NewTable("Table")) {

        icon = new ImageIcon(getClass.getResource("/icon/accessories-calculator.png"))

      }
    )

    super.register
    this.start

  }

  override def unregister {

    this ! Abort
    super.unregister

  }

  protected def frame = widebase.workspace.ide.app.plugin.frame

  protected def action(msg: Any) {

    msg match {

      case Abort => exit

      case (table: Table, tableName: String) =>

        if(frame.pagedPane.selection.index == -1 ||
           !frame.pagedPane.selection.page.content.isInstanceOf[PagedPane] ||
           frame.pagedPane.selection.page.content.isInstanceOf[PreferenceManager])
         frame.pagedPane.add(content = configure(new PagedPane))
        else if(frame.pagedPane.selection.page.content.asInstanceOf[PagedPane].selection.index == -1)
          configure(frame.pagedPane.selection.page.content.asInstanceOf[PagedPane])

        val pane = frame.pagedPane.selection.page.content.asInstanceOf[PagedPane]

        val panel = {

          val frame = new TableFrame(widebase.ui.table.tablePanel(table))

          val panel = frame.contents.head

          frame.dispose

          panel

        }

        frame.pagedPane.selection.page.content.asInstanceOf[PagedPane].pages +=
          new TabbedDesktopPane.Page(
            tableName,
            new ImageIcon(getClass.getResource("/icon/accessories-calculator.png")),
            new ScrollPane { contents = panel } )

    }
  }

  protected def configure(pane: PagedPane) = {

    // Key bindings

    for(i <- 0 to 8)
      util.bind(
        pane,
        KeyStroke.getKeyStroke(KeyEvent.VK_1 + i, InputEvent.ALT_MASK),
        "PageSelection" + i,
        () => { if(i < pane.pages.size) pane.selection.index = i } )

    util.bind(
      pane,
      KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.ALT_MASK),
      "PageSelection9",
      () => { if(9 < pane.pages.size) pane.selection.index = 9 } )

    // Popup menu

    if(!pane.popupMenu.sub.contains("New")) {

      pane.popupMenu.prepend(UUID.randomUUID.toString)
      pane.popupMenu.prepend("New" -> new Menu("New"))

    }

    pane.popupMenu.sub("New").prepend(
      "Table" -> new MenuItem(new NewTable("Table")) {

        icon = new ImageIcon(getClass.getResource("/icon/accessories-calculator.png.png"))

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

object Plugin {

  val category = "Core"
  val homepage = "http://widebase.github.com/"
  val id = classOf[Plugin].getPackage.getName
  val name = "Widebase IDE Table"

}

