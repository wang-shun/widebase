package widebase.ui.ide

import event._
import prefs.PreferenceSheet
import worksheet. { EditPanel, Workbook, Worksheet }

import de.sciss.scalainterpreter.LogPane

import java.awt. { BorderLayout, Desktop }
import java.net.URL

import javax.swing. { ImageIcon, JOptionPane, JSplitPane }

import moreswing.swing.TabbedDesktopPane
import moreswing.swing.i18n. { LMainFrame, LocaleManager }

import net.liftweb.common.Logger

import scala.swing. { BorderPanel, Dimension }
import scala.util.control.Breaks. { break, breakable}
import scala.xml. { NodeSeq, XML }

import widebase.ui.toolkit.event. { LocaleChanged, LookAndFeelChanged }

/** Main frame.
 * 
 * @author myst3r10n
 */
class AppFrame extends LMainFrame with Logger {

  title = "app.title"
  preferredSize = new Dimension(1024, 768)

  menuBar = new AppMenuBar

  lazy val toolBar = new AppToolBar
  lazy val workbook = new Workbook

  val panel = new scala.swing.BorderPanel {

    add(toolBar, BorderPanel.Position.North)
    add(workbook, BorderPanel.Position.Center)

  }

  val logPane = LogPane().makeDefault()

  val splitPane = new JSplitPane(
    JSplitPane.VERTICAL_SPLIT,
    panel.peer,
    logPane.component)

  peer.add(splitPane)

  listenTo(this, menuBar, toolBar, workbook)
  reactions += {

    case Exit =>
      EditPanel.actor ! EditPanel.Abort
      AppFrame.this.dispose

    case event: LocaleChanged =>
      App.prefs.put("app.locale", event.replaced)

    case event: LookAndFeelChanged =>
      App.prefs.put("app.laf", event.replaced.getClassName)

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

    case WidebaseAbout =>
      JOptionPane.showMessageDialog(
        AppFrame.this.peer,
        "Widebase IDE",
        "About...",
        JOptionPane.INFORMATION_MESSAGE)

    case WidebaseHandbook =>
      if(Desktop.isDesktopSupported)
        Desktop.getDesktop.browse(new URL(
          "http://widebase.github.com/docs/books/widebase-handbook/html/index.html").toURI)
      else
        JOptionPane.showMessageDialog(
          AppFrame.this.peer,
          "Class java.awt.Desktop.browse not supported",
          "Environment",
          JOptionPane.ERROR_MESSAGE)

    case WidebaseHome =>
      if(Desktop.isDesktopSupported)
        Desktop.getDesktop.browse(new URL("http://widebase.github.com/").toURI)
      else
        JOptionPane.showMessageDialog(
          AppFrame.this.peer,
          "Class java.awt.Desktop.browse not supported",
          "Environment",
          JOptionPane.ERROR_MESSAGE)

  }
}

