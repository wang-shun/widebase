package widebase.ui.ide.chart

import de.sciss.scalainterpreter.CodePane

import java.awt. { BorderLayout, Point }
import java.awt.event. { InputEvent, KeyEvent }
import java.util.UUID

import javax.swing. { ImageIcon, JOptionPane, KeyStroke }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n.LocaleManager

import scala.swing. { BorderPanel, Button, ComboBox, ScrollPane, Separator }
import scala.swing.event.ButtonClicked

import widebase.ui.chart. { ChartFrame, ChartPanel }

import widebase.ui.workspace. {

  Action,
  FrameLike,
  Menu,
  MenuItem,
  PagedPane,
  PageMenu,
  PreferenceManager

}

import widebase.ui.workspace.runtime.PluginLike

class Plugin(frame: FrameLike) extends PluginLike {

  import widebase.ui.workspace. { runtime, util }

  class NewChart(title0: String = "") extends Action(title0) {

    import widebase.ui.workspace

    toolTip = "New_Chart"

    def apply {

      val charts = new ComboBox(Seq("candle", "highlow", "plot", "scatter"))
      val codePane = CodePane(CodePane.Config())

      val panel = new BorderPanel {

        add(charts, BorderPanel.Position.North)
        peer.add(codePane.component, BorderLayout.CENTER)

      }

      if(JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(
        frame.peer,
        panel.peer,
        "Chart",
        JOptionPane.OK_CANCEL_OPTION))
        runtime.queue.add(Some("""

          widebase.ui.workspace.runtime.plugin("""" + scope + """").asInstanceOf[widebase.ui.ide.chart.Plugin].NewChart(widebase.ui.chart.""" + charts.selection.item + """Panel(""" + codePane.editor.getText + """))

        """))
    }
  }

  object NewChart {

    import scala.util.control.Breaks. { break, breakable }

    def apply(panel0: ChartPanel) {

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

            if(page.title == LocaleManager.text("Chart_?", count)) {

              found = false
              break

            }
          }
        }
      } while(!found)

      val panel = {

        val frame = new ChartFrame(panel0)

        val panel = frame.contents.head

        frame.dispose

        panel

      }

      pane.pages += new TabbedDesktopPane.Page(
        LocaleManager.text("Chart_?", count),
        new ImageIcon(getClass.getResource("/icon/kchart.png")),
        new ScrollPane { contents = panel } )

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
        "Table" -> new MenuItem(new NewChart("Table")) {

          icon = new ImageIcon(getClass.getResource("/icon/kchart.png.png"))

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

  val label = "Widebase IDE Chart"
  val scope = "widebase.ui.ide.chart"

  override def option = None

  override def register {

    frame.menuBar("File").sub("New").prepend(
      "Chart" -> new MenuItem(new NewChart("Chart")) {

        icon = new ImageIcon(getClass.getResource("/icon/kchart.png"))

      }
    )

    frame.toolBar.prepend(
      "Chart" -> new Button(new NewChart) {

        icon = new ImageIcon(getClass.getResource("/icon/kchart.png"))

      }
    )

    frame.pagedPane.popupMenu.sub("New").prepend(
      "Chart" -> new MenuItem(new NewChart("Chart")) {

        icon = new ImageIcon(getClass.getResource("/icon/kchart.png"))

      }
    )

    super.register

  }
}

