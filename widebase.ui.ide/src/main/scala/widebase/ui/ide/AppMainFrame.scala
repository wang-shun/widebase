package widebase.ui.ide

import event._
import prefs.PreferenceSheet
import worksheet. { Workbook, Worksheet }

import de.sciss.scalainterpreter.LogPane

import java.awt.BorderLayout

import javax.swing. { ImageIcon, JSplitPane }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n. { LMainFrame, LocaleManager }

import net.liftweb.common.Logger

import scala.swing. { BorderPanel, Dimension }
import scala.util.control.Breaks. { break, breakable}
import scala.xml. { NodeSeq, XML }

/** Main frame.
 * 
 * @author myst3r10n
 */
class AppMainFrame extends LMainFrame with Logger {

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  lazy val workbook = new Workbook

  menuBar = new AppMainMenuBar
  val toolBar = new AppMainToolBar

  val panel = new scala.swing.BorderPanel {

    peer.add(toolBar, BorderLayout.NORTH)
    add(workbook, BorderPanel.Position.Center)

  }

  val splitPane = new JSplitPane(
    JSplitPane.VERTICAL_SPLIT,
    panel.peer,
    LogPane().makeDefault().component)

  peer.add(splitPane)

  listenTo(this, menuBar, toolBar, workbook)
  reactions += {

    case Exit => dispose

    case NewEdit =>
      if(workbook.selection.index == -1 ||
         !workbook.selection.page.content.isInstanceOf[Worksheet])
        workbook.newWorksheet

      workbook.selection.page.content.asInstanceOf[Worksheet].newEdit

    case NewWorksheet => workbook.newWorksheet

    case NewPreferenceSheet =>

      object Break extends Throwable

      try {

        workbook.pages.foreach(page =>
          if(page.content.isInstanceOf[PreferenceSheet]) {

            workbook.selection.page = page
            throw Break

          }
        )

        workbook.pages += new TabbedDesktopPane.Page(
          LocaleManager.text("Preferences"),
          new ImageIcon(getClass.getResource("/icon/preferences-system.png")),
          new PreferenceSheet)

      } catch { case Break => }
  }
}

